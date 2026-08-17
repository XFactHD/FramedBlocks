package io.github.xfacthd.framedblocks.client.model.wrapping;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.internal.ModelWrapperRegistrar;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMergers;
import io.github.xfacthd.framedblocks.client.model.template.GeometryTemplateManager;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedCopyingFramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedEmptyFramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedFramedDoubleBlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

final class ModelWrapperRegistrarImpl implements ModelWrapperRegistrar {
    @Override
    public void wrapSingle(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger) {
        Preconditions.checkArgument(block.value() instanceof IFramedBlock, "Cannot register model wrapper for non-IFramedBlock");
        wrapCustom(block, ctx -> new UnbakedFramedBlockStateModel(ctx, geometryFactory, false), stateMerger);
    }

    @Override
    public void wrapSingle(Holder<Block> block, GeometryTemplateSpec templateSpec, StateMerger stateMerger) {
        wrapSingle(block, GeometryTemplateManager.createTemplatedGeometryFactory(templateSpec), stateMerger);
    }

    @Override
    public void wrapDouble(Holder<Block> block, StateMerger stateMerger) {
        Preconditions.checkArgument(block.value() instanceof IFramedDoubleBlock, "Cannot register double model wrapper for non-IFramedDoubleBlock");
        wrapCustom(block, UnbakedFramedDoubleBlockStateModel::new, stateMerger);
    }

    @Override
    public void wrapCustom(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger) {
        ModelWrappingManager.register(block, new ModelWrappingHandler(block, modelFactory, stateMerger));
    }

    @Override
    public void copyModels(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger) {
        wrapCustom(block, ctx -> new UnbakedCopyingFramedBlockStateModel(ctx, srcBlock.value()), stateMerger);
    }

    @Override
    public void wrapEmpty(Holder<Block> block) {
        wrapCustom(block, UnbakedEmptyFramedBlockStateModel::new, StateMergers.ignoreAll(block));
    }

    @Override
    public <T> void wrapStandalone(StandaloneWrapperKey<T> wrapperKey, GeometryFactory geometryFactory, StandaloneModelFactory<T> modelFactory, StateMerger stateMerger) {
        Holder<Block> block = wrapperKey.block();
        Preconditions.checkArgument(block.value() instanceof IFramedBlock, "Cannot register model wrapper for non-IFramedBlock");
        ModelFactory blockModelFactory = ctx -> new UnbakedFramedBlockStateModel(ctx, geometryFactory, wrapperKey.isForceCt());
        ModelWrappingManager.register(wrapperKey, new StandaloneModelWrappingHandler<>(block, blockModelFactory, stateMerger, modelFactory));
    }

    @Override
    public <T> void wrapStandalone(StandaloneWrapperKey<T> wrapperKey, GeometryTemplateSpec templateSpec, StandaloneModelFactory<T> modelFactory, StateMerger stateMerger) {
        wrapStandalone(wrapperKey, GeometryTemplateManager.createTemplatedGeometryFactory(templateSpec), modelFactory, stateMerger);
    }

    @Override
    public void overrideBlockModelFactory(Holder<Block> block, Function<BlockState, BlockModel.Unbaked> modelFactory) {
        ModelWrappingManager.getHandler(block.value()).overrideBlockModelFactory(modelFactory);
    }
}
