package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_FLOOR_BOARD)
public final class FloorBoardSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(BlockType.FRAMED_FLOOR_BOARD)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) && adjState.getBlock() == state.getBlock())
        {
            boolean top = state.getValue(FramedProperties.TOP);
            boolean adjTop = adjState.getValue(FramedProperties.TOP);
            return top == adjTop;
        }

        return false;
    }
}
