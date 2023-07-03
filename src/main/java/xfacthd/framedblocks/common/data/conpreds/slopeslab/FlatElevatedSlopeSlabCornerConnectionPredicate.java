package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class FlatElevatedSlopeSlabCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo)
        {
            return true;
        }
        else if (side == facing)
        {
            return edge == dirTwo || edge == facing.getCounterClockWise();
        }
        else if (side == facing.getCounterClockWise())
        {
            return edge == dirTwo || edge == facing;
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing)
        {
            return edge == facing.getClockWise();
        }
        else if (side == facing.getCounterClockWise())
        {
            return edge == facing.getOpposite();
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return !Utils.isY(edge);
        }
        else if (side == dirTwo)
        {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        return false;
    }
}
