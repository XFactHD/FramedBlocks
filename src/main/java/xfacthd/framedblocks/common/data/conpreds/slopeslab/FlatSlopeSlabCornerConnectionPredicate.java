package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatSlopeSlabCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (top == topHalf)
        {
            if (side == dirTwo)
            {
                return true;
            }
            else if (side == facing || side == facing.getCounterClockWise())
            {
                return edge == dirTwo;
            }
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing)
        {
            return edge == facing.getCounterClockWise();
        }
        else if (top != topHalf && side == dirTwo.getOpposite())
        {
            return true;
        }
        else if (side == facing.getCounterClockWise())
        {
            return edge == facing;
        }
        else if (side == dirTwo || side == facing.getClockWise() || side == facing.getOpposite())
        {
            if (top != topHalf && (edge == facing.getOpposite() || edge == facing.getClockWise()))
            {
                return true;
            }
            return edge == facing || edge == facing.getCounterClockWise();
        }
        return false;
    }
}
