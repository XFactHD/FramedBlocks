package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;

public final class FloorBoardSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (Utils.isY(side))
        {
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }
        else if (adjState.getBlock() == state.getBlock())
        {
            boolean top = state.getValue(FramedProperties.TOP);
            boolean adjTop = adjState.getValue(FramedProperties.TOP);
            return top == adjTop;
        }

        return false;
    }
}
