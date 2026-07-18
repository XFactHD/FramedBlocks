package io.github.xfacthd.framedblocks.client.model.template;

import com.mojang.math.OctahedralGroup;
import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.template.PostModifierProvider;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import it.unimi.dsi.fastutil.objects.Reference2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

record TemplatedGeometryFactory(GeometryTemplateSpecImpl specGetter) implements GeometryFactory {
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private static final QuadSpec[] NO_SPECS = new QuadSpec[0];

    @Override
    public Geometry create(Context ctx) {
        GeometryTemplateSpecEntry geoSpec = specGetter.get(ctx.state());
        QuadSpec[][] quadSpecs = new QuadSpec[6][];
        boolean transformAll = extractQuadSpecs(quadSpecs, ctx, geoSpec);
        TemplateOverlayProvider overlay = geoSpec.overlayFactory().create(ctx);
        if (geoSpec.appenderConfig() != null) {
            return new AppendingTemplatedGeometry(ctx.state(), ctx.baseModel(), geoSpec, quadSpecs, transformAll, overlay);
        } else {
            return new BasicTemplatedGeometry(geoSpec, quadSpecs, transformAll, overlay);
        }
    }

    private static boolean extractQuadSpecs(QuadSpec[][] quadSpecs, GeometryFactory.Context ctx, GeometryTemplateSpecEntry geoSpec) {
        Map<Direction, List<QuadSpec>> specsByDir = new EnumMap<>(Direction.class);
        BiConsumer<Direction, QuadSpec> specAppender = (normal, spec) ->
                specsByDir.computeIfAbsent(normal, _ -> new ArrayList<>()).add(spec);
        StateCache stateCache = ctx.state().framedblocks$getCache();
        boolean copycat = CopycatStyleBlock.tryIsCopycatStyle(ctx.state());
        PostModifierProvider postModifiers = geoSpec.postModifiers();
        boolean transformAll = false;
        for (SourceFile sourceFile : geoSpec.sourceFiles()) {
            GeometryTemplate template = GeometryTemplateManager.getTemplate(sourceFile.id().id());
            for (GeometryTemplate.Cube cube : template.cubes()) {
                OctahedralGroup xform = geoSpec.transform().compose(sourceFile.transform());
                Vector3fc from = transform(cube.from(), xform);
                Vector3fc to = transform(cube.to(), xform);
                for (GeometryTemplate.Face face : cube.faces()) {
                    Direction normal = xform.rotate(face.normal());
                    boolean cullable = face.cullable();
                    if (stateCache.isFullFace(normal)) {
                        if (cullable) {
                            continue;
                        }
                        transformAll = true;
                    }
                    if (copycat && geoSpec.isCopycatFace(normal, cullable)) {
                        extractCopycatFace(from, to, normal, cullable, postModifiers, specAppender);
                    } else {
                        extractStandardFace(from, to, normal, cullable, postModifiers, specAppender);
                    }
                }
            }
        }

        Arrays.fill(quadSpecs, NO_SPECS);
        for (Map.Entry<Direction, List<QuadSpec>> entry : specsByDir.entrySet()) {
            quadSpecs[entry.getKey().ordinal()] = entry.getValue().toArray(QuadSpec[]::new);
        }
        return transformAll;
    }

    private static void extractStandardFace(
            Vector3fc from,
            Vector3fc to,
            Direction normal,
            boolean cullable,
            @Nullable PostModifierProvider postModifiers,
            BiConsumer<Direction, QuadSpec> specAppender
    ) {
        List<QuadModifier.Modifier> modifiers = new ArrayList<>();
        List<EdgeCut> edges = new ArrayList<>();
        for (Direction edge : DirUtils.getAxisTubeFaces(normal.getAxis())) {
            float width = length(edge, from, to);
            if (Mth.equal(width, 1F)) {
                continue;
            }
            if (Mth.equal(width, 0F)) {
                return;
            }
            edges.add(new EdgeCut(edge, width));
        }
        createStandardCuts(modifiers, edges, normal);
        float depth = length(normal, from, to);
        if (!Mth.equal(depth, 1F)) {
            modifiers.add(Modifiers.setPosition(depth));
        }
        Direction cullFace = cullable ? normal : null;
        if (postModifiers != null) {
            postModifiers.collectPostModifiers(normal, cullable, modifiers::add);
            cullFace = postModifiers.transformCullFace(normal, cullFace);
        }
        specAppender.accept(normal, new QuadSpec(modifiers, cullFace));
    }

    private static void createStandardCuts(List<QuadModifier.Modifier> modifiers, List<EdgeCut> edges, Direction normal) {
        if (edges.size() == 1) {
            EdgeCut cut = edges.getFirst();
            modifiers.add(Modifiers.cut(cut.edge, cut.length));
            return;
        }

        if (edges.size() == 2) {
            EdgeCut cutOne = edges.getFirst();
            EdgeCut cutTwo = edges.getLast();

            if (cutOne.edge == cutTwo.edge.getOpposite()) {
                if (Mth.equal(cutOne.length, cutTwo.length)) {
                    modifiers.add(Modifiers.cut(cutOne.edge.getAxis(), cutOne.length));
                } else {
                    boolean onePos = DirUtils.isPositive(cutOne.edge);
                    float lenNeg = onePos ? cutTwo.length : cutOne.length;
                    float lenPos = onePos ? cutOne.length : cutTwo.length;
                    modifiers.add(Modifiers.cut(cutOne.edge.getAxis(), lenNeg, lenPos));
                }
                return;
            }
        }

        if (DirUtils.isY(normal)) {
            float minX = 0F;
            float minZ = 0F;
            float maxX = 1F;
            float maxZ = 1F;
            for (EdgeCut cut : edges) {
                switch (cut.edge) {
                    case NORTH -> minZ = 1F - cut.length;
                    case SOUTH -> maxZ = cut.length;
                    case WEST -> minX = 1F - cut.length;
                    case EAST -> maxX = cut.length;
                }
            }
            modifiers.add(Modifiers.cutTopBottom(minX, minZ, maxX, maxZ));
        } else {
            float minXZ = 0F;
            float minY = 0F;
            float maxXZ = 1F;
            float maxY = 1F;
            for (EdgeCut cut : edges) {
                switch (cut.edge) {
                    case DOWN -> minY = 1F - cut.length;
                    case UP -> maxY = cut.length;
                    case NORTH, WEST -> minXZ = 1F - cut.length;
                    case SOUTH, EAST -> maxXZ = cut.length;
                }
            }
            modifiers.add(Modifiers.cutSide(minXZ, minY, maxXZ, maxY));
        }
    }

    private static void extractCopycatFace(
            Vector3fc from,
            Vector3fc to,
            Direction normal,
            boolean cullable,
            @Nullable PostModifierProvider postModifiers,
            BiConsumer<Direction, QuadSpec> specAppender
    ) {
        Reference2FloatMap<Direction> edgeOffsets = new Reference2FloatArrayMap<>();
        for (Direction edge : DirUtils.getAxisTubeFaces(normal.getAxis())) {
            float width = length(edge, from, to);
            if (Mth.equal(width, 1F)) {
                continue;
            }
            if (Mth.equal(width, 0F)) {
                return;
            }
            edgeOffsets.put(edge, 1F - width);
        }

        QuadModifier.Modifier depthMod = null;
        float depth = length(normal, from, to);
        if (!Mth.equal(depth, 1F)) {
            depthMod = Modifiers.setPosition(depth);
        }

        List<List<QuadModifier.Modifier>> modifierStacks = new ArrayList<>();
        Iterator<Direction.Axis> perpAxes = DirUtils.getPerpendicularAxes(normal.getAxis()).iterator();

        Direction.Axis axisOne = perpAxes.next();
        Direction.Axis axisTwo = perpAxes.next();

        float offOneNeg = edgeOffsets.getOrDefault(axisOne.getNegative(), 0F);
        float offOnePos = edgeOffsets.getOrDefault(axisOne.getPositive(), 0F);
        float offTwoNeg = edgeOffsets.getOrDefault(axisTwo.getNegative(), 0F);
        float offTwoPos = edgeOffsets.getOrDefault(axisTwo.getPositive(), 0F);

        boolean fullOne = Mth.equal(offOneNeg, 0F) && Mth.equal(offOnePos, 0F);
        boolean fullTwo = Mth.equal(offTwoNeg, 0F) && Mth.equal(offTwoPos, 0F);
        if (fullOne && fullTwo) {
            if (depthMod != null || postModifiers != null) {
                modifierStacks.add(new ArrayList<>());
            }
        } else if (fullOne) {
            createCopycatCuts(modifierStacks, axisTwo, offTwoNeg, offTwoPos);
        } else if (fullTwo) {
            createCopycatCuts(modifierStacks, axisOne, offOneNeg, offOnePos);
        } else {
            createCopycatCuts(modifierStacks, axisOne, axisTwo, offOneNeg, offOnePos, offTwoNeg, offTwoPos);
        }

        List<QuadModifier.Modifier> postModList = List.of();
        Direction cullFace = cullable ? normal : null;
        if (postModifiers != null) {
            postModList = new ArrayList<>();
            postModifiers.collectPostModifiers(normal, cullable, postModList::add);
            cullFace = postModifiers.transformCullFace(normal, cullFace);
        }

        for (List<QuadModifier.Modifier> modifiers : modifierStacks) {
            if (depthMod != null) {
                modifiers.add(depthMod);
            }
            if (!postModList.isEmpty()) {
                modifiers.addAll(postModList);
            }
            specAppender.accept(normal, new QuadSpec(modifiers, cullFace));
        }
    }

    private static void createCopycatCuts(List<List<QuadModifier.Modifier>> modifierStacks, Direction.Axis axis, float offNeg, float offPos) {
        appendModifierStack(modifierStacks, Modifiers.cutCopycat(axis.getNegative(), offNeg, offPos));
        appendModifierStack(modifierStacks, Modifiers.cutCopycat(axis.getPositive(), offNeg, offPos));
    }

    private static void createCopycatCuts(
            List<List<QuadModifier.Modifier>> modifierStacks,
            Direction.Axis axisOne,
            Direction.Axis axisTwo,
            float offNegOne,
            float offPosOne,
            float offNegTwo,
            float offPosTwo
    ) {
        appendModifierStack(modifierStacks, Modifiers.cutCopycat(axisOne.getNegative(), axisTwo.getNegative(), offNegOne, offPosOne, offNegTwo, offPosTwo));
        appendModifierStack(modifierStacks, Modifiers.cutCopycat(axisOne.getNegative(), axisTwo.getPositive(), offNegOne, offPosOne, offNegTwo, offPosTwo));
        appendModifierStack(modifierStacks, Modifiers.cutCopycat(axisOne.getPositive(), axisTwo.getNegative(), offNegOne, offPosOne, offNegTwo, offPosTwo));
        appendModifierStack(modifierStacks, Modifiers.cutCopycat(axisOne.getPositive(), axisTwo.getPositive(), offNegOne, offPosOne, offNegTwo, offPosTwo));
    }

    private static void appendModifierStack(List<List<QuadModifier.Modifier>> modifierStacks, QuadModifier.Modifier modifier) {
        ArrayList<QuadModifier.Modifier> modifiers = new ArrayList<>();
        modifiers.add(modifier);
        modifierStacks.add(modifiers);
    }

    private static float length(Direction dir, Vector3fc from, Vector3fc to) {
        float coordFrom = select(dir, from);
        float coordTo = select(dir, to);
        float length = switch (dir.getAxisDirection()) {
            case POSITIVE -> Math.max(coordFrom, coordTo);
            case NEGATIVE -> 16F - Math.min(coordFrom, coordTo);
        };
        return length / 16F;
    }

    private static float select(Direction dir, Vector3fc vec) {
        return switch (dir.getAxis()) {
            case X -> vec.x();
            case Y -> vec.y();
            case Z -> vec.z();
        };
    }

    private static Vector3fc transform(Vector3fc vec, OctahedralGroup rotation) {
        if (rotation == OctahedralGroup.IDENTITY) {
            return vec;
        }

        Vector3f newVec = new Vector3f(vec).add(-8F, -8F, -8F);
        rotation.transformation().transform(newVec);
        return newVec.add(8F, 8F, 8F);
    }

    private record EdgeCut(Direction edge, float length) { }
}
