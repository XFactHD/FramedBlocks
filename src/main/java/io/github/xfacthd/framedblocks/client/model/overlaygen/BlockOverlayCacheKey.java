package io.github.xfacthd.framedblocks.client.model.overlaygen;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

record BlockOverlayCacheKey(
        @Nullable BlockState outerState,
        @Nullable BlockState partState,
        boolean secondPart,
        BlockOverlay overlay,
        boolean forceTranslucent,
        TriState ambientOcclusion,
        boolean emissive,
        List<Bounds> bounds,
        int tintIndex
)
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final @Nullable Direction[] DIRECTIONS_WITH_NULL = Arrays.copyOf(DIRECTIONS, DIRECTIONS.length + 1);

    @Nullable
    static BlockOverlayCacheKey compute(
            @Nullable BlockState outerState,
            BlockState partState,
            boolean secondPart,
            BlockOverlay overlay,
            List<BlockStateModelPart> sourceParts,
            boolean emissive,
            boolean fastPath,
            int tintIndex
    )
    {
        BlockOverlayMetaCache.Entry metadata = BlockOverlayMetaCache.get(overlay, partState);
        List<Bounds> bounds = computeBounds(metadata, sourceParts, fastPath);
        if (bounds.isEmpty()) return null;

        if (outerState != null)
        {
            outerState = ModelWrappingManager.tryGetStateMerger(outerState.getBlock()).apply(outerState);
        }
        BlockState keyPartState = metadata.stateDependent() ? partState : null;
        boolean forceTranslucent = computeForceTranslucent(overlay, sourceParts);
        TriState ambientOcclusion = computeAmbientOcclusion(sourceParts);
        return new BlockOverlayCacheKey(outerState, keyPartState, secondPart, overlay, forceTranslucent, ambientOcclusion, emissive, bounds, tintIndex);
    }

    private static boolean computeForceTranslucent(BlockOverlay overlay, List<BlockStateModelPart> parts)
    {
        if (overlay.translucent())
        {
            return true;
        }
        for (BlockStateModelPart part : parts)
        {
            if ((part.materialFlags() & BakedQuad.FLAG_TRANSLUCENT) != 0)
            {
                return true;
            }
        }
        return false;
    }

    private static TriState computeAmbientOcclusion(List<BlockStateModelPart> parts)
    {
        for (BlockStateModelPart part : parts)
        {
            TriState ao = part.ambientOcclusion();
            if (ao != TriState.DEFAULT)
            {
                return ao;
            }
        }
        return TriState.DEFAULT;
    }

    private static List<Bounds> computeBounds(BlockOverlayMetaCache.Entry metadata, List<BlockStateModelPart> parts, boolean fastPath)
    {
        Object2ObjectMap<QuadSetKey, List<BakedQuad>> quadsByNormal = null;
        for (BlockStateModelPart part : parts)
        {
            for (Direction face : fastPath ? DIRECTIONS : DIRECTIONS_WITH_NULL)
            {
                for (BakedQuad quad : part.getQuads(face))
                {
                    Direction normalDir = quad.direction();
                    if (!metadata.isFaceAffected(normalDir))
                    {
                        continue;
                    }

                    int normal = quad.bakedNormals().normal(0);
                    if (BakedNormals.isUnspecified(normal))
                    {
                        normal = BakedNormals.computeQuadNormal(quad.position0(), quad.position1(), quad.position2(), quad.position3());
                    }
                    if (quadsByNormal == null)
                    {
                        quadsByNormal = new Object2ObjectOpenHashMap<>();
                    }
                    quadsByNormal.computeIfAbsent(new QuadSetKey(face, normalDir, normal), _ -> new ArrayList<>()).add(quad);
                }
            }
        }
        if (quadsByNormal == null || quadsByNormal.isEmpty())
        {
            return List.of();
        }
        return fastPath ? computeBoundsFast(quadsByNormal) : computeBoundsFull(quadsByNormal, metadata);
    }

    private static List<Bounds> computeBoundsFast(Object2ObjectMap<QuadSetKey, List<BakedQuad>> quadsByNormal)
    {
        List<Bounds> bounds = new ArrayList<>(quadsByNormal.size());
        for (Object2ObjectMap.Entry<QuadSetKey, List<BakedQuad>> entry : quadsByNormal.object2ObjectEntrySet())
        {
            QuadSetKey key = entry.getKey();
            List<BakedQuad> quads = entry.getValue();
            Set<QuadBounds> quadBounds = new LinkedHashSet<>(quads.size());
            for (BakedQuad quad : quads)
            {
                quadBounds.add(new QuadBounds(quad.position0(), quad.position1(), quad.position2(), quad.position3()));
            }
            bounds.add(new Bounds(key.cullFace, key.normalDir, key.normal, SurfaceBounds.FULL, quadBounds));
        }
        return bounds;
    }

    private static List<Bounds> computeBoundsFull(Object2ObjectMap<QuadSetKey, List<BakedQuad>> quadsByNormal, BlockOverlayMetaCache.Entry metadata)
    {
        Map<Direction, Set<Direction>> edgesByFace = metadata.edgesByFace();
        List<Bounds> bounds = new ArrayList<>(quadsByNormal.size());
        for (Object2ObjectMap.Entry<QuadSetKey, List<BakedQuad>> entry : quadsByNormal.object2ObjectEntrySet())
        {
            QuadSetKey key = entry.getKey();
            List<BakedQuad> quads = entry.getValue();
            Direction normalDir = key.normalDir;

            SurfaceBounds surfaceBounds = SurfaceBounds.EMPTY;
            if (edgesByFace.containsKey(normalDir))
            {
                float minX = 1F;
                float minY = 1F;
                float maxX = 0F;
                float maxY = 0F;
                Direction.Axis axisX = DirUtils.isX(normalDir) ? Direction.Axis.Z : Direction.Axis.X;
                Direction.Axis axisY = DirUtils.isY(normalDir) ? Direction.Axis.Z : Direction.Axis.Y;
                // TODO: expand bounds extraction to handle tilted edges to allow aligning overlays on the tilted edges of sloped blocks, discarding edges with an angle > 45°
                for (BakedQuad quad : quads)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        Vector3fc pos = quad.position(i);
                        minX = Math.min(minX, (float) axisX.choose(pos.x(), pos.y(), pos.z()));
                        minY = Math.min(minY, (float) axisY.choose(pos.x(), pos.y(), pos.z()));
                        maxX = Math.max(maxX, (float) axisX.choose(pos.x(), pos.y(), pos.z()));
                        maxY = Math.max(maxY, (float) axisY.choose(pos.x(), pos.y(), pos.z()));
                    }
                }
                surfaceBounds = new SurfaceBounds(minX, minY, maxX, maxY);
            }

            Set<QuadBounds> quadBounds = new LinkedHashSet<>(quads.size());
            for (BakedQuad quad : quads)
            {
                quadBounds.add(new QuadBounds(quad.position0(), quad.position1(), quad.position2(), quad.position3()));
            }

            bounds.add(new Bounds(key.cullFace, normalDir, key.normal, surfaceBounds, quadBounds));
        }
        return bounds;
    }

    private record QuadSetKey(@Nullable Direction cullFace, Direction normalDir, int normal) { }

    record Bounds(@Nullable Direction cullFace, Direction normalDir, int normal, SurfaceBounds surfaceBounds, Set<QuadBounds> quadBounds) { }

    record SurfaceBounds(float minX, float minY, float maxX, float maxY)
    {
        private static final SurfaceBounds EMPTY = new SurfaceBounds(0F, 0F, 0F, 0F);
        private static final SurfaceBounds FULL = new SurfaceBounds(0F, 0F, 1F, 1F);
    }

    record QuadBounds(Vector3fc pos0, Vector3fc pos1, Vector3fc pos2, Vector3fc pos3) implements OverlayQuadGenerator.VertexCoordProvider
    {
        @Override
        public Vector3fc pos(int index)
        {
            return switch (index)
            {
                case 0 -> pos0;
                case 1 -> pos1;
                case 2 -> pos2;
                case 3 -> pos3;
                default -> throw new IndexOutOfBoundsException(index);
            };
        }
    }
}
