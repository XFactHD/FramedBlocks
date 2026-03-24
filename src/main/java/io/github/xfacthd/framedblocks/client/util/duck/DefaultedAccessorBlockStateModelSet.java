package io.github.xfacthd.framedblocks.client.util.duck;

import io.github.xfacthd.framedblocks.mixin.client.AccessorBlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

@SuppressWarnings("unused") // Used via interface injection
public interface DefaultedAccessorBlockStateModelSet extends AccessorBlockStateModelSet
{
    @Override
    default Map<BlockState, BlockStateModel> framedblocks$getModelByState()
    {
        throw new AssertionError();
    }
}
