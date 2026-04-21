package io.github.xfacthd.framedblocks.api.model.standalone;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface StandaloneModelFactory<T> {
    T create(Map<BlockState, BlockStateModel> modes);
}
