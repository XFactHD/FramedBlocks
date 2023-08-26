package xfacthd.framedblocks.common.data.skippreds.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_FENCE_GATE)
public final class FenceGateSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(value = { BlockType.FRAMED_FENCE_GATE, BlockType.FRAMED_WALL }, oneWay = true)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FenceGateBlock.FACING);
        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return adjState.getBlock() == state.getBlock() || adjState.getBlock() == FBContent.BLOCK_FRAMED_WALL.get();
        }
        return false;
    }
}
