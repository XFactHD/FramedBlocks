package io.github.xfacthd.framedblocks.common.block;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface PrismCornerBlock {
    default BlockState applyOffset(BlockState state, BlockPlaceContext context) {
        if (isOffsetOnOddPos()) {
            return state.setValue(FramedProperties.OFFSET, context.getClickedPos().getY() % 2 != 0);
        } else {
            return state.setValue(FramedProperties.OFFSET, context.getClickedPos().getY() % 2 == 0);
        }
    }

    boolean isOffsetOnOddPos();
}
