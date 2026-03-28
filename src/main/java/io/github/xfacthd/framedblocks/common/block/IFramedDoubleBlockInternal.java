package io.github.xfacthd.framedblocks.common.block;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IFramedDoubleBlockInternal extends IFramedDoubleBlock {
    @Override
    default FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedDoubleBlockEntity(FBContent.BE_TYPE_FRAMED_DOUBLE_BLOCK.value(), pos, state);
    }
}
