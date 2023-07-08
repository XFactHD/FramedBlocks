package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class SlopeSlabConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = topHalf ? Direction.UP : Direction.DOWN;
        boolean solidFace = top == topHalf;

        if (solidFace && side == dirTwo)
        {
            return true;
        }
        else if (side == facing)
        {
            return edge == dirTwo;
        }
        else if (solidFace && side.getAxis() == facing.getClockWise().getAxis())
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

        if (side == facing)
        {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        else if (top != topHalf && side == dirTwo.getOpposite())
        {
            return true;
        }
        else if (side.getAxis() == facing.getClockWise().getAxis())
        {
            return edge == facing;
        }
        else if (side == facing.getOpposite() || side == dirTwo)
        {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        return false;
    }
}
