package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatInnerSlopeSlabCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        boolean fullEdge = top == topHalf;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (fullEdge && side == dirTwo)
        {
            return true;
        }
        else if (side == facing || side == facing.getCounterClockWise())
        {
            return edge == dirTwo;
        }
        else if (fullEdge && side == facing.getOpposite() || side == facing.getClockWise())
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
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing || side == facing.getCounterClockWise())
        {
            return !Utils.isY(edge);
        }
        else if (top != topHalf && side == dirTwo.getOpposite())
        {
            return true;
        }
        else if (side == facing.getOpposite())
        {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        else if (side == facing.getClockWise())
        {
            return edge.getAxis() == facing.getAxis();
        }
        else if (side == dirTwo)
        {
            if (top == topHalf && (edge == facing || edge == facing.getCounterClockWise()))
            {
                return true;
            }
            return edge == facing.getClockWise() || edge == facing.getOpposite();
        }
        return false;
    }
}
