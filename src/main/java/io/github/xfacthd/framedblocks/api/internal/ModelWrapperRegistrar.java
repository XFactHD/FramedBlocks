package io.github.xfacthd.framedblocks.api.internal;

import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public interface ModelWrapperRegistrar {
    void wrapSingle(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger);

    void wrapSingle(Holder<Block> block, GeometryTemplateSpec templateSpec, StateMerger stateMerger);

    void wrapDouble(Holder<Block> block, StateMerger stateMerger);

    void wrapCustom(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger);

    void copyModels(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger);

    void wrapEmpty(Holder<Block> block);

    <T> void wrapStandalone(StandaloneWrapperKey<T> wrapperKey, GeometryFactory geometryFactory, StandaloneModelFactory<T> modelFactory, StateMerger stateMerger);

    <T> void wrapStandalone(StandaloneWrapperKey<T> wrapperKey, GeometryTemplateSpec templateSpec, StandaloneModelFactory<T> modelFactory, StateMerger stateMerger);

    void overrideBlockModelFactory(Holder<Block> block, Function<BlockState, BlockModel.Unbaked> modelFactory);
}
