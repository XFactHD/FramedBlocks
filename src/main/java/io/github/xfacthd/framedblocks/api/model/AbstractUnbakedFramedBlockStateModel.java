package io.github.xfacthd.framedblocks.api.model;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.AuxModelProvider;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/// Base class for unbaked blockstate models with wrapping capabilities.
public abstract class AbstractUnbakedFramedBlockStateModel implements BlockStateModel.UnbakedRoot {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected final BlockState state;
    protected final BlockStateModel.UnbakedRoot baseModel;
    protected final ModelDebugName debugName;
    private final Map<String, SingleVariant.Unbaked> auxModels;
    private final Object bakingLock = new Object();
    @Nullable
    private volatile AbstractFramedBlockStateModel cachedBakingResult = null;

    /// @param ctx The context this model is being constructed in
    protected AbstractUnbakedFramedBlockStateModel(ModelFactory.Context ctx) {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.debugName = state::toString;
        this.auxModels = ctx.auxModels();
    }

    /// {@return the baked blockstate model for the given context}
    /// This method is called at most once, regardless of how many blockstates use this model.
    ///
    /// @param context The context the model is being baked in
    /// @param baker   The baker the model is being baked with
    protected abstract AbstractFramedBlockStateModel bakeCached(GeometryFactory.Context context, ModelBaker baker);

    @Override
    public final AbstractFramedBlockStateModel bake(BlockState ignoredState, ModelBaker baker) {
        // This cannot be converted to ModelBaker.SharedOperationKey due to the wrapped model potentially also using that
        if (cachedBakingResult == null) {
            synchronized (bakingLock) {
                if (cachedBakingResult == null) {
                    Map<String, BlockStateModel> bakedAuxModels = new HashMap<>(auxModels.size());
                    for (Map.Entry<String, SingleVariant.Unbaked> entry : auxModels.entrySet()) {
                        bakedAuxModels.put(entry.getKey(), entry.getValue().bake(baker));
                    }
                    BlockStateModel missingModel = baker.compute(ModelUtils.MISSING_MODEL_KEY);

                    // The provided state must not be used for baking or wrapping as it may not be the one previously
                    // computed by deduplication through a StateMerger and may therefore break part data retrieval
                    // in the part models of double blocks
                    BlockStateModel bakedBase = baseModel.bake(state, baker);
                    AuxModelProvider auxModels = new AuxModelProviderImpl(state, bakedAuxModels, missingModel);
                    MaterialLookup textures = MaterialLookup.bindMaterialBaker(baker.materials(), debugName);
                    GeometryFactory.Context context = new GeometryFactory.Context(state, bakedBase, auxModels, textures);
                    cachedBakingResult = bakeCached(context, baker);
                }
            }
        }
        return Objects.requireNonNull(cachedBakingResult);
    }

    @Override
    public final Object visualEqualityGroup(BlockState state) {
        return this.state;
    }

    @Override
    public final void resolveDependencies(Resolver resolver) {
        baseModel.resolveDependencies(resolver);
        for (SingleVariant.Unbaked auxModel : auxModels.values()) {
            auxModel.resolveDependencies(resolver);
        }
        resolveSpecialDependencies(resolver);
    }

    /// Resolve additional model dependencies.
    ///
    /// @param resolver The resolver to mark dependencies with
    protected void resolveSpecialDependencies(Resolver resolver) {}

    private record AuxModelProviderImpl(BlockState state, Map<String, BlockStateModel> auxModels, BlockStateModel missingModel) implements AuxModelProvider {
        @Override
        public BlockStateModel getModel(String key) {
            BlockStateModel model = auxModels.get(key);
            if (model != null) {
                return model;
            }

            LOGGER.warn("AbstractUnbakedFramedBlockModel for {} has no aux model with key {}, returning missing model", state, key);
            return missingModel;
        }
    }
}
