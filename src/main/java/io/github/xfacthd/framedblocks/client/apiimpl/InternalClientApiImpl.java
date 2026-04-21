package io.github.xfacthd.framedblocks.client.apiimpl;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.AuxModelProvider;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.client.model.FramedBlockStateModelPart;
import io.github.xfacthd.framedblocks.client.model.quadmap.QuadMapBuilderInternal;
import io.github.xfacthd.framedblocks.client.model.ReinforcementModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.item.FramedBlockItemModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.FramedBlockModelDefinition;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedCopyingFramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedFramedDoubleBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingHandler;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.client.model.wrapping.StandaloneModelWrappingHandler;
import io.github.xfacthd.framedblocks.client.render.special.ModelBasedOutlineRenderer;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import io.github.xfacthd.framedblocks.client.util.ClientTaskQueue;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class InternalClientApiImpl implements InternalClientAPI {
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    @Override
    public void registerModelWrapper(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger) {
        Preconditions.checkArgument(block.value() instanceof IFramedBlock, "Cannot register model wrapper for non-IFramedBlock");
        registerSpecialModelWrapper(block, ctx -> new UnbakedFramedBlockStateModel(ctx, geometryFactory), stateMerger);
    }

    @Override
    public void registerDoubleModelWrapper(Holder<Block> block, ItemModelInfo itemModelInfo, StateMerger stateMerger) {
        Preconditions.checkArgument(block.value() instanceof IFramedDoubleBlock, "Cannot register double model wrapper for non-IFramedDoubleBlock");
        registerSpecialModelWrapper(block, ctx -> new UnbakedFramedDoubleBlockStateModel(ctx, itemModelInfo), stateMerger);
    }

    @Override
    public void registerSpecialModelWrapper(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger) {
        ModelWrappingManager.register(block, new ModelWrappingHandler(block, modelFactory, stateMerger));
    }

    @Override
    public void registerCopyingModelWrapper(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger) {
        registerSpecialModelWrapper(block, ctx -> new UnbakedCopyingFramedBlockStateModel(ctx, srcBlock.value()), stateMerger);
    }

    @Override
    public <T> void registerStandaloneModelWrapper(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory geometryFactory,
            StandaloneModelFactory<T> modelFactory,
            StateMerger stateMerger
    ) {
        Holder<Block> block = wrapperKey.block();
        Preconditions.checkArgument(block.value() instanceof IFramedBlock, "Cannot register model wrapper for non-IFramedBlock");
        ModelFactory blockModelFactory = ctx -> new UnbakedFramedBlockStateModel(ctx, geometryFactory);
        ModelWrappingManager.register(wrapperKey, new StandaloneModelWrappingHandler<>(block, blockModelFactory, stateMerger, modelFactory));
    }

    @Override
    public void enqueueClientTask(int delay, Runnable task) {
        ClientTaskQueue.enqueueClientTask(delay, task);
    }

    @Override
    public ItemModel.Unbaked createFramedBlockItemModel(Block block, BlockItemModelProvider modelProvider, Either<Identifier, ItemTransforms> modelOrXform) {
        return new FramedBlockItemModel.Unbaked(block, modelProvider, modelOrXform);
    }

    @Override
    public ExtendedBlockStateModelPart makeBlockModelPart(QuadMapBuilder quadMap, TriState partAO, Material.Baked particleMaterial, @Nullable BlockState shaderState) {
        if (shaderState == AIR) {
            shaderState = null;
        }
        return new FramedBlockStateModelPart(((QuadMapBuilderInternal) quadMap).build(), partAO, particleMaterial, shaderState);
    }

    @Override
    public BlockStateModelDispatcher createFramedBlockDefinition(
            Either<BlockStateModelDispatcher, SingleVariant.Unbaked> wrapped,
            Map<String, SingleVariant.Unbaked> auxModels,
            Optional<StandaloneWrapperKey<?>> wrapperKey
    ) {
        return new BlockStateModelDispatcher(new FramedBlockModelDefinition(wrapped, auxModels, wrapperKey));
    }

    @Override
    public Supplier<BlockStateModel> createBlockItemModelProviderForGeometry(BlockState state, BlockState srcState, GeometryFactory geometry, ModelBaker baker) {
        return () -> {
            BlockStateModel baseModel = ModelUtils.getModel(srcState);
            if (baseModel instanceof AbstractFramedBlockStateModel framedModel) {
                baseModel = framedModel.getBaseModel();
            }
            GeometryFactory.Context ctx = new GeometryFactory.Context(state, baseModel, AuxModelProvider.invalid(), MaterialLookup.runtime());
            ReinforcementModel reinforcement = ReinforcementModel.getOrCreate(baker);
            return new FramedBlockStateModel(ctx, geometry.create(ctx), reinforcement);
        };
    }

    @Override
    public OutlineRenderer<?> createModelBasedOutlineRenderer(Block block) {
        return new ModelBasedOutlineRenderer(block);
    }

    @Override
    public MaterialLookup getRuntimeMaterialLookup() {
        return RuntimeMaterialBaker.INSTANCE;
    }

    @Override
    public void registerLoadedCachingModel(CachingModel model) {
        CacheCleaner.registerLoadedCachingModel(model);
    }
}
