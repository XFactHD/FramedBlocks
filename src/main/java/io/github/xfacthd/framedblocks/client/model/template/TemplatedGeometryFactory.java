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
        for (Direction edge : DirUtils.getAxisTubeFaces(normal.getAxis())) {
            float width = length(edge, from, to);
            if (Mth.equal(width, 1F)) {
                continue;
            }
            if (Mth.equal(width, 0F)) {
                return;
            }
            modifiers.add(Modifiers.cut(edge, width));
        }
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

        float depth = length(normal, from, to);
        List<QuadModifier.Modifier> initialModifiers = new ArrayList<>();
        if (!Mth.equal(depth, 1F)) {
            initialModifiers.add(Modifiers.setPosition(depth));
        }

        List<List<QuadModifier.Modifier>> modifierStacks = new ArrayList<>();
        Iterator<Direction.Axis> perpAxes = DirUtils.getPerpendicularAxes(normal.getAxis()).iterator();

        Direction.Axis axisOne = perpAxes.next();
        float offOneNeg = edgeOffsets.getOrDefault(axisOne.getNegative(), 0F);
        float offOnePos = edgeOffsets.getOrDefault(axisOne.getPositive(), 0F);
        if (offOneNeg > 0 || offOnePos > 0) {
            createCopycatCuts(modifierStacks, initialModifiers, axisOne, offOneNeg, offOnePos, true);
        } else {
            modifierStacks.add(initialModifiers);
        }

        Direction.Axis axisTwo = perpAxes.next();
        float offTwoNeg = edgeOffsets.getOrDefault(axisTwo.getNegative(), 0F);
        float offTwoPos = edgeOffsets.getOrDefault(axisTwo.getPositive(), 0F);
        if (offTwoNeg > 0 || offTwoPos > 0) {
            int prevSize = modifierStacks.size();
            for (int i = 0; i < prevSize; i++) {
                createCopycatCuts(modifierStacks, modifierStacks.get(i), axisTwo, offTwoNeg, offTwoPos, false);
            }
        }

        List<QuadModifier.Modifier> postModList = List.of();
        Direction cullFace = cullable ? normal : null;
        if (postModifiers != null) {
            postModList = new ArrayList<>();
            postModifiers.collectPostModifiers(normal, cullable, postModList::add);
            cullFace = postModifiers.transformCullFace(normal, cullFace);
        }
        for (List<QuadModifier.Modifier> modifiers : modifierStacks) {
            if (!postModList.isEmpty()) {
                modifiers.addAll(postModList);
            }
            specAppender.accept(normal, new QuadSpec(modifiers, cullFace));
        }
    }

    private static void createCopycatCuts(
            List<List<QuadModifier.Modifier>> modifierStacks,
            List<QuadModifier.Modifier> baseModifiers,
            Direction.Axis axis,
            float offsetNeg,
            float offsetPos,
            boolean addSecond
    ) {
        float halfLen = (1F - offsetNeg - offsetPos) / 2F;

        List<QuadModifier.Modifier> modsNeg = new ArrayList<>(baseModifiers);
        modsNeg.add(Modifiers.cut(axis.getPositive(), halfLen));
        modsNeg.add(Modifiers.offset(axis.getPositive(), offsetNeg));
        modifierStacks.add(modsNeg);

        baseModifiers.add(Modifiers.cut(axis.getNegative(), halfLen));
        baseModifiers.add(Modifiers.offset(axis.getNegative(), offsetPos));
        if (addSecond) {
            modifierStacks.add(baseModifiers);
        }
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
        rotation.permutation().permuteVector(newVec);
        newVec.x = newVec.x * (rotation.inverts(Direction.Axis.X) ? -1F : 1F);
        newVec.y = newVec.y * (rotation.inverts(Direction.Axis.Y) ? -1F : 1F);
        newVec.z = newVec.z * (rotation.inverts(Direction.Axis.Z) ? -1F : 1F);
        return newVec.add(8F, 8F, 8F);
    }
}
