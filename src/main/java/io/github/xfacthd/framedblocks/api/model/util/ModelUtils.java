package io.github.xfacthd.framedblocks.api.model.util;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.quad.ExtMutableQuad;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/// Provides various helpers for working with models.
public final class ModelUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    /// Shared operations key for creating a blockstate model of the missing cube model.
    public static final ModelBaker.SharedOperationKey<BlockStateModel> MISSING_MODEL_KEY = makeSharedOpsKey(
            baker -> new SingleVariant(baker.missingBlockModelPart())
    );

    /// Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
    /// onto the UV range they occupy as given by the values at 'uv1' and 'uv2',
    /// calculates the target UV coordinate corresponding to the value of 'coordTo'
    /// and stores it at 'uvTo' index.
    ///
    /// @param quad    The mutable quad being operated on
    /// @param coord1  The first coordinate
    /// @param coord2  The second coordinate
    /// @param coordTo The target coordinate, must lie between coord1 and coord2
    /// @param uv1     The first UV texture coordinate index
    /// @param uv2     The second UV texture coordinate index
    /// @param uvTo    The target UV texture coordinate index
    /// @param vAxis   Whether the modification should happen on the V axis or the U axis
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

    /// {@return the blockstate model associated with the given blockstate}
    ///
    /// @param state The state to get the model for
    public static BlockStateModel getModel(BlockState state) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
    }

    /// Returns the framed blockstate model associated with the given blockstate or a wrapper
    /// of the state's model if said model is not a framed blockstate model.
    ///
    /// @param state The state to get the model for
    /// @return the framed blockstate model for the given state
    public static AbstractFramedBlockStateModel getFramedBlockModel(BlockState state) {
        BlockStateModel model = getModel(state);
        if (model instanceof AbstractFramedBlockStateModel framedModel) {
            return framedModel;
        }
        LOGGER.error("Could not resolve AbstractFramedBlockStateModel for {}, got {} instead. Using fallback path, expect visual issues", state, model.getClass());
        return new DelegateFramedBlockStateModel(model, state);
    }

    /// {@return the fluid model associated with the given fluidstate}
    ///
    /// @param state The fluidstate to get the model for
    public static FluidModel getFluidModel(FluidState state) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state);
    }

    /// {@return a memoized supplier of the blockstate model associated with the given blockstate}
    ///
    /// @param state The state to get the model for
    public static Supplier<BlockStateModel> getModelDeferred(BlockState state) {
        return Lazy.of(() -> getModel(state));
    }

    /// Guess the actual cull-face of quads returned by [BlockStateModelPart#getQuads(Direction)] with a `null` cullface
    /// (i.e. supposedly uncullable quads) and filter them to return the ones applicable to the given cullface and touching
    /// the block volume's edge. This fixes blocks becoming invisible when mods forget to specify cull-faces in their models.
    ///
    /// Heavily based on [Embeddium's quad flag calculation](https://github.com/embeddedt/embeddium/blob/72ba934b27fa35856a0a64f3aa6c867592b2e54f/src/main/java/me/jellysquid/mods/sodium/client/model/quad/properties/ModelQuadFlags.java#L41-L115),
    /// licensed under LGPL v3.
    ///
    /// @param modelPart The model part to retrieve the quads from
    /// @param side      The cullface to filter for
    /// @return filtered quads applicable to the given cullface
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

    /// {@return a shared operation key using the given function for its computation}
    ///
    /// @param operation The operation to perform
    @SuppressWarnings({ "Convert2Lambda", "Anonymous2MethodRef" })
    public static <T> ModelBaker.SharedOperationKey<T> makeSharedOpsKey(Function<ModelBaker, T> operation) {
        return new ModelBaker.SharedOperationKey<>() {
            @Override
            public T compute(ModelBaker baker) {
                return operation.apply(baker);
            }
        };
    }

    /// Register the provided model for loading without baking it. Useful for models whose unbaked representation is
    /// later retrieved and baked on-the-fly.
    ///
    /// @param event The registration event
    /// @param model The model to register
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

    /// {@return the provided model's geometry key, filtering out keys that are the queried model and therefore don't need to be included in the cache key}
    ///
    /// @param model  The model to query
    /// @param level  The level to query the model with
    /// @param pos    The position to query the model with
    /// @param state  The block state to query the model with
    /// @param random The random source to query the model with
    public static @Nullable Object getGeometryKeyFiltered(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        Object geometryKey = model.createGeometryKey(level, pos, state, random);
        if (geometryKey == model) {
            geometryKey = null;
        }
        return geometryKey;
    }

    private ModelUtils() { }
}
