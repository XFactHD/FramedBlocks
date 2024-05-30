package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface ICollapsibleCopycatBlockEntity
{
    int getFaceOffset(BlockState state, Direction side);

    int getPackedOffsets(BlockState state);
}
