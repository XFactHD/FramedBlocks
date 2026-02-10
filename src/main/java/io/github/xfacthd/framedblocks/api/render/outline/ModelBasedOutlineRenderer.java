package io.github.xfacthd.framedblocks.api.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An {@link OutlineRenderer} which derives the lines from the block's geometry instead of requiring them
 * to be manually specified for the individual block.
 */
public final class ModelBasedOutlineRenderer implements SimpleOutlineRenderer
{
    private static final List<ModelBasedOutlineRenderer> RENDERERS = new ArrayList<>();
    private static final @Nullable Direction[] DIRECTIONS = Arrays.copyOf(Direction.values(), 7);
    private static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();
    private static final List<BlockModelPart> SCRATCH_LIST = new ObjectArrayList<>();

    private final Map<BlockState, float[]> cachedVertices = new IdentityHashMap<>();
    private final StateMerger stateMerger;

    public ModelBasedOutlineRenderer(Block block)
    {
        this.stateMerger = ModelWrappingManager.tryGetStateMerger(block);
        RENDERERS.add(this);
    }

    @Override
    public void draw(BlockState state, LineDrawer drawer)
    {
        state = stateMerger.apply(state);
        float[] vertices = cachedVertices.computeIfAbsent(state, ModelBasedOutlineRenderer::computeVertices);
        drawer.drawLines(vertices);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state) { }

    @ApiStatus.Internal
    public static void clearCaches()
    {
        RENDERERS.forEach(renderer -> renderer.cachedVertices.clear());
    }

    private static float[] computeVertices(BlockState state)
    {
        BlockStateModel model = ModelUtils.getModel(state);
        model.collectParts(EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO, state, RANDOM, SCRATCH_LIST);
        // Collect all unique quads with their vertex positions and quad normal (assumes all quads are planar)
        Set<Quad> uniqueQuads = new HashSet<>();
        for (BlockModelPart part : SCRATCH_LIST)
        {
            for (Direction dir : DIRECTIONS)
            {
                for (BakedQuad quad : part.getQuads(dir))
                {
                    Vector3fc v0 = quad.position0();
                    Vector3fc v1 = quad.position1();
                    Vector3fc v2 = quad.position2();
                    Vector3fc v3 = quad.position3();
                    int normal = quad.bakedNormals().normal(0);
                    uniqueQuads.add(new Quad(v0, v1, v2, v3, normal));
                }
            }
        }
        SCRATCH_LIST.clear();

        // Collect all edges grouped by the quad normal they originate from
        Int2ObjectMap<Object2IntMap<Line>> linesByNormal = new Int2ObjectOpenHashMap<>();
        for (Quad quad : uniqueQuads)
        {
            Object2IntMap<Line> lines = linesByNormal.computeIfAbsent(quad.normal, $ -> new Object2IntOpenHashMap<>());
            appendEdge(quad.v0, quad.v1, lines);
            appendEdge(quad.v2, quad.v3, lines);
            appendEdge(quad.v0, quad.v3, lines);
            appendEdge(quad.v1, quad.v2, lines);
        }

        // Collect all unique lines, eliminating identical lines which originate from multiple quads of identical or opposing normals
        Set<Line> uniqueLines = new HashSet<>();
        for (Int2ObjectMap.Entry<Object2IntMap<Line>> entry : linesByNormal.int2ObjectEntrySet())
        {
            int normal = entry.getIntKey();
            int oppNormal = BakedNormals.pack(-BakedNormals.unpackX(normal), -BakedNormals.unpackY(normal), -BakedNormals.unpackZ(normal));
            Object2IntMap<Line> oppEntry = linesByNormal.getOrDefault(oppNormal, Object2IntMaps.emptyMap());

            // Merge parallel line segments which share a point into one line
            Object2IntMap<Line> lines = entry.getValue();
            tryMergeSegments(
                    lines.keySet(),
                    line -> lines.computeInt(line, ModelBasedOutlineRenderer::increment),
                    line -> lines.computeInt(line, ModelBasedOutlineRenderer::decrementAndEliminate),
                    ModelBasedOutlineRenderer::checkNonOverlapping
            );

            // Collect lines which appear exactly once on this normal and the opposing normal
            Set<Line> uniqueLinesOnNormal = new HashSet<>();
            for (Object2IntMap.Entry<Line> line : lines.object2IntEntrySet())
            {
                if (line.getIntValue() == 1 && !oppEntry.containsKey(line.getKey()))
                {
                    uniqueLinesOnNormal.add(line.getKey());
                }
            }
            // "XOR" partially overlapping line segments
            tryMergeSegments(uniqueLinesOnNormal, ModelBasedOutlineRenderer::checkOneContainsTwo);
            uniqueLines.addAll(uniqueLinesOnNormal);
        }

        // Merge parallel line segments which share a point into one line
        tryMergeSegments(uniqueLines, ModelBasedOutlineRenderer::checkNonOverlapping);

        // Remove lines which overlap another longer line
        removeOverlappingSegments(uniqueLines);

        // Pack unique lines into float array
        float[] packedLines = new float[uniqueLines.size() * 6];
        int idx = 0;
        for (Line line : uniqueLines)
        {
            packedLines[idx    ] = line.x1;
            packedLines[idx + 1] = line.y1;
            packedLines[idx + 2] = line.z1;
            packedLines[idx + 3] = line.x2;
            packedLines[idx + 4] = line.y2;
            packedLines[idx + 5] = line.z2;
            idx += 6;
        }
        return packedLines;
    }

    private static void appendEdge(Vector3fc v1, Vector3fc v2, Object2IntMap<Line> lines)
    {
        Line line = new Line(v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z());
        if (line.isNonZero())
        {
            lines.computeInt(line, ModelBasedOutlineRenderer::increment);
        }
    }

    private static void tryMergeSegments(Set<Line> lines, OverlapCheck check)
    {
        tryMergeSegments(lines, lines::add, lines::remove, check);
    }

    private static void tryMergeSegments(Set<Line> lines, Consumer<Line> adder, Consumer<Line> remover, OverlapCheck check)
    {
        boolean merged;
        do
        {
            merged = false;
            outer: for (Line lineOne : lines)
            {
                for (Line lineTwo : lines)
                {
                    if (lineOne == lineTwo) continue;

                    int shared = lineOne.getSharedPointWith(lineTwo);
                    if (shared == -1) continue;

                    if (!lineOne.isParallelTo(lineTwo)) continue;
                    if (!check.test(lineOne, lineTwo, shared)) continue;

                    remover.accept(lineOne);
                    remover.accept(lineTwo);
                    adder.accept(lineOne.merge(lineTwo, shared));
                    merged = true;
                    break outer;
                }
            }
        } while (merged);
    }

    private static void removeOverlappingSegments(Set<Line> lines)
    {
        boolean removed;
        do
        {
            removed = false;
            outer: for (Line lineOne : lines)
            {
                for (Line lineTwo : lines)
                {
                    if (lineOne == lineTwo) continue;
                    if (!lineOne.isParallelTo(lineTwo)) continue;
                    if (!lineOne.contains(lineTwo.x1, lineTwo.y1, lineTwo.z1)) continue;
                    if (!lineOne.contains(lineTwo.x2, lineOne.y2, lineTwo.z2)) continue;

                    lines.remove(lineTwo);
                    removed = true;
                    break outer;
                }
            }
        } while (removed);
    }

    private static boolean checkNonOverlapping(Line lineOne, Line lineTwo, int shared)
    {
        return !lineOne.containsPointOf(lineTwo, shared, false) && !lineTwo.containsPointOf(lineOne, shared, true);
    }

    private static boolean checkOneContainsTwo(Line lineOne, Line lineTwo, int shared)
    {
        return lineOne.containsPointOf(lineTwo, shared, false);
    }

    private static <T> Integer increment(T key, @Nullable Integer value)
    {
        return value == null ? 1 : value + 1;
    }

    @Nullable
    private static <T> Integer decrementAndEliminate(T key, @Nullable Integer value)
    {
        if (value == null) return null;

        value--;
        return value > 0 ? value : null;
    }

    private record Quad(Vector3fc v0, Vector3fc v1, Vector3fc v2, Vector3fc v3, int normal)
    {
        Quad
        {
            if (BakedNormals.isUnspecified(normal))
            {
                normal = BakedNormals.computeQuadNormal(v0, v1, v2, v3);
            }
        }
    }

    private record Line(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        Line
        {
            x1 = quantize(x1);
            y1 = quantize(y1);
            z1 = quantize(z1);
            x2 = quantize(x2);
            y2 = quantize(y2);
            z2 = quantize(z2);

            if (compare(x1, y1, z1, x2, y2, z2) > 0)
            {
                float tx = x1;
                float ty = y1;
                float tz = z1;
                x1 = x2;
                y1 = y2;
                z1 = z2;
                x2 = tx;
                y2 = ty;
                z2 = tz;
            }
        }

        private static float quantize(float value)
        {
            return Math.round(value * 64F) / 64F;
        }

        private static int compare(float x1, float y1, float z1, float x2, float y2, float z2)
        {
            int compX = Float.compare(x1, x2);
            if (compX != 0) return compX;
            int compY = Float.compare(y1, y2);
            if (compY != 0) return compY;
            return Float.compare(z1, z2);
        }

        boolean isNonZero()
        {
            return x1 != x2 || y1 != y2 || z1 != z2;
        }

        int getSharedPointWith(Line other)
        {
            if (other.x1 == x1 && other.y1 == y1 && other.z1 == z1) return 0x11;
            if (other.x2 == x2 && other.y2 == y2 && other.z2 == z2) return 0x22;
            if (other.x1 == x2 && other.y1 == y2 && other.z1 == z2) return 0x21;
            if (other.x2 == x1 && other.y2 == y1 && other.z2 == z1) return 0x12;
            return -1;
        }

        boolean contains(float x, float y, float z)
        {
            Vector3f tmp = new Vector3f();
            float d1 = tmp.set(x1, y1, z1).distance(x, y, z);
            float d2 = tmp.set(x2, y2, z2).distance(x, y, z);
            float d3 = tmp.set(x1, y1, z1).distance(x2, y2, z2);
            return Mth.equal(d1 + d2, d3);
        }

        boolean containsPointOf(Line other, int shared, boolean otherFirst)
        {
            if (otherFirst)
            {
                shared >>>= 4;
            }
            shared &= 0xF;
            return switch (shared)
            {
                case 0x1 -> contains(other.x2, other.y2, other.z2);
                case 0x2 -> contains(other.x1, other.y1, other.z1);
                default -> throw new IllegalArgumentException("Invalid shared point: " + shared);
            };
        }

        boolean isParallelTo(Line other)
        {
            Vector3f diffOne = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
            Vector3f diffTwo = new Vector3f(other.x2 - other.x1, other.y2 - other.y1, other.z2 - other.z1);
            return Mth.equal(0F, diffOne.cross(diffTwo).lengthSquared());
        }

        Line merge(Line other, int shared)
        {
            return switch (shared)
            {
                case 0x11 -> new Line(x2, y2, z2, other.x2, other.y2, other.z2);
                case 0x22 -> new Line(x1, y1, z1, other.x1, other.y1, other.z1);
                case 0x21 -> new Line(x1, y1, z1, other.x2, other.y2, other.z2);
                case 0x12 -> new Line(x2, y2, z2, other.x1, other.y1, other.z1);
                default -> throw new IllegalArgumentException("Invalid shared point: " + shared);
            };
        }
    }

    private interface OverlapCheck
    {
        boolean test(Line lineOne, Line lineTwo, int shared);
    }
}
