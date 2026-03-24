package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface ModelFactory
{
    AbstractUnbakedFramedBlockStateModel create(ModelFactory.Context ctx);

    default void reset() { }

    record Context(BlockState state, BlockStateModel.UnbakedRoot baseModel, Map<String, SingleVariant.Unbaked> auxModels) { }
}
