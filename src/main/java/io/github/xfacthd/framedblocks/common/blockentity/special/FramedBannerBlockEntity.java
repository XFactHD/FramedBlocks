package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FramedBannerBlockEntity extends FramedBlockEntity {
    public FramedBannerBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_BANNER.value(), pos, state);
    }
}
