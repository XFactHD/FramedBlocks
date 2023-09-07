package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_WALL_BOARD)
public final class WallBoardSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(BlockType.FRAMED_WALL_BOARD)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        if (side.getAxis() != dir.getAxis() && adjState.getBlock() == state.getBlock())
        {
            Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
            return adjDir == dir;
        }

        return false;
    }
}
