package xfacthd.framedblocks.common.data.skippreds.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;

public final class FenceGateSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FenceGateBlock.FACING);
        if ((side == dir.getClockWise() || side == dir.getCounterClockWise()) && adjState.getBlock() == FBContent.blockFramedWall.get())
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }
}
