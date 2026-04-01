package io.github.xfacthd.framedblocks.api.model.util;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.quad.ExtMutableQuad;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.TriState;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModelUtils {
    public static final ModelBaker.SharedOperationKey<BlockStateModel> MISSING_MODEL_KEY = makeSharedOpsKey(
            baker -> new SingleVariant(baker.missingBlockModelPart())
    );

    /**
     * Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
     * onto the UV range they occupy as given by the values at 'uv1' and 'uv2' in the 'uv'
     * array, calculates the target UV coordinate corresponding to the value of 'coordTo'
     * and places it at 'uvTo' in the 'uv' array
     * @param quad The {@link ExtMutableQuad} being operated on
     * @param coord1 The first coordinate
     * @param coord2 The second coordinate
     * @param coordTo The target coordinate, must lie between coord1 and coord2
     * @param uv1 The first UV texture coordinate
     * @param uv2 The second UV texture coordinate
     * @param uvTo The target UV texture coordinate
     * @param vAxis Whether the modification should happen on the V axis or the U axis
     */
    public static void remapUV(
            ExtMutableQuad quad,
            float coord1,
            float coord2,
            float coordTo,
            int uv1,
            int uv2,
            int uvTo,
            boolean vAxis
    ) {
        float coordMin = Math.min(coord1, coord2);
        float coordMax = Math.max(coord1, coord2);

        int uvIdx = quad.uvRotated() != vAxis ? 1 : 0;

        float uvAbs1 = quad.uvComponent(uv1, uvIdx);
        float uvAbs2 = quad.uvComponent(uv2, uvIdx);
        float uvAbsMin = Math.min(uvAbs1, uvAbs2);
        float uvAbsMax = Math.max(uvAbs1, uvAbs2);
        boolean invert = ((coord2 > coord1) ^ (uvAbs2 > uvAbs1)) != vAxis;

        if (coordTo == coordMin) {
            quad.setUvComponent(uvTo, uvIdx, (invert) ? uvAbsMax : uvAbsMin);
        } else if (coordTo == coordMax) {
            quad.setUvComponent(uvTo, uvIdx, (invert) ? uvAbsMin : uvAbsMax);
        } else {
            float mult = (coordTo - coordMin) / (coordMax - coordMin);
            if (invert) mult = 1F - mult;
            quad.setUvComponent(uvTo, uvIdx, Mth.lerp(mult, uvAbsMin, uvAbsMax));
        }
    }

    public static boolean isQuadRotated(ExtMutableQuad data) {
        return (Mth.equal(data.uvComponent(0, 1), data.uvComponent(1, 1)) || Mth.equal(data.uvComponent(3, 1), data.uvComponent(2, 1))) &&
               (Mth.equal(data.uvComponent(1, 0), data.uvComponent(2, 0)) || Mth.equal(data.uvComponent(0, 0), data.uvComponent(3, 0)));
    }

    public static BlockStateModel getModel(BlockState state) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
    }

    public static AbstractFramedBlockStateModel getFramedBlockModel(BlockState state) {
        BlockStateModel model = getModel(state);
        if (model instanceof AbstractFramedBlockStateModel framedModel) {
            return framedModel;
        }
        return new DelegateFramedBlockStateModel(model, state);
    }

    public static FluidModel getFluidModel(FluidState state) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state);
    }

    public static Supplier<BlockStateModel> getModelDeferred(BlockState state) {
        return Lazy.of(() -> getModel(state));
    }

    public static ExtendedBlockStateModelPart makeModelPart(QuadMapBuilder quadMap, TriState partAO, Material.Baked particleMaterial, @Nullable BlockState shaderState) {
        return InternalClientAPI.INSTANCE.makeBlockModelPart(quadMap, partAO, particleMaterial, shaderState);
    }

    /**
     * Guess the cull-face of quads returned by {@link BlockStateModelPart#getQuads(Direction)}
     * with a {@code null} side (i.e. supposedly uncullable quads) and filter them to return the ones applicable to the given
     * {@link Direction} and touching the block edge. This fixes blocks becoming invisible when mods forget to specify
     * cull-faces in their models
     * <p>
     * Heavily based on <a href="https://github.com/embeddedt/embeddium/blob/72ba934b27fa35856a0a64f3aa6c867592b2e54f/src/main/java/me/jellysquid/mods/sodium/client/model/quad/properties/ModelQuadFlags.java#L41-L115">Embeddium's quad flag calculation</a>,
     * licensed under LGPL v3
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static List<BakedQuad> getFilteredNullQuads(BlockStateModelPart modelPart, Direction side) {
        List<BakedQuad> nullQuads = modelPart.getQuads(null);
        if (nullQuads.isEmpty()) {
            return Collections.emptyList();
        }

        List<BakedQuad> quadsOut = null;
        for (int i = 0; i < nullQuads.size(); i++) {
            BakedQuad quad = nullQuads.get(i);

            // Filter out quads pointing completely the wrong way early
            if (quad.direction() != side) {
                continue;
            }

            float minX = 32F;
            float minY = 32F;
            float minZ = 32F;
            float maxX = -32F;
            float maxY = -32F;
            float maxZ = -32F;

            for (int vert = 0; vert < 4; ++vert) {
                Vector3fc pos = quad.position(vert);

                minX = Math.min(minX, pos.x());
                minY = Math.min(minY, pos.y());
                minZ = Math.min(minZ, pos.z());
                maxX = Math.max(maxX, pos.x());
                maxY = Math.max(maxY, pos.y());
                maxZ = Math.max(maxZ, pos.z());
            }

            boolean positive = DirUtils.isPositive(side);
            boolean aligned = switch(side.getAxis()) {
                case X -> minX == maxX && (positive ? maxX > 0.9999F : minX < 0.0001F);
                case Y -> minY == maxY && (positive ? maxY > 0.9999F : minY < 0.0001F);
                case Z -> minZ == maxZ && (positive ? maxZ > 0.9999F : minZ < 0.0001F);
            };

            if (aligned) {
                if (quadsOut == null) {
                    quadsOut = new ArrayList<>();
                }
                quadsOut.add(quad);
            }
        }
        return quadsOut != null ? quadsOut : Collections.emptyList();
    }

    @SuppressWarnings({ "Convert2Lambda", "Anonymous2MethodRef" })
    public static <T> ModelBaker.SharedOperationKey<T> makeSharedOpsKey(Function<ModelBaker, T> operation) {
        return new ModelBaker.SharedOperationKey<>() {
            @Override
            public T compute(ModelBaker baker) {
                return operation.apply(baker);
            }
        };
    }

    /**
     * Register the provided model for loading without baking it. Useful for models whose unbaked representation is
     * later retrieved and baked on-the-fly.
     *
     * @param event The registration event
     * @param model The model to register
     */
    public static void registerStandaloneForLoading(ModelEvent.RegisterStandalone event, Identifier model) {
        event.register(new StandaloneModelKey<>(model::toString), new UnbakedStandaloneModel<Unit>() {
            @Override
            public Unit bake(ModelBaker baker, ModelDebugName debugName) {
                return Unit.INSTANCE;
            }

            @Override
            public void resolveDependencies(Resolver resolver) {
                resolver.markDependency(model);
            }
        });
    }

    private ModelUtils() { }
}
