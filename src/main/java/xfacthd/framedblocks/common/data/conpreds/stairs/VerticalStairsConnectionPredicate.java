package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalStairsConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        boolean top = type.isTop();
        boolean bottom = type.isBottom();
        boolean fwd = type.isForward();
        boolean ccw = type.isCounterClockwise();

        if (side == facing)
        {
            if (!fwd)
            {
                return true;
            }
            return edge != facing.getClockWise() && ((top && edge != Direction.UP) || (bottom && edge != Direction.DOWN));
        }
        else if (side == facing.getCounterClockWise())
        {
            if (!ccw)
            {
                return true;
            }
            return edge != facing.getOpposite() && ((top && edge != Direction.UP) || (bottom && edge != Direction.DOWN));
        }
        else if (side == facing.getOpposite())
        {
            return !ccw && edge == facing.getCounterClockWise();
        }
        else if (side == facing.getClockWise())
        {
            return !fwd && edge == facing;
        }
        else if (side == Direction.UP)
        {
            if (!top)
            {
                return edge == facing || edge == facing.getCounterClockWise();
            }
            return (!fwd && edge == facing) || (!ccw && edge == facing.getCounterClockWise());
        }
        else if (side == Direction.DOWN)
        {
            if (!bottom)
            {
                return edge == facing || edge == facing.getCounterClockWise();
            }
            return (!fwd && edge == facing) || (!ccw && edge == facing.getCounterClockWise());
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        boolean top = type.isTop();
        boolean bottom = type.isBottom();
        boolean fwd = type.isForward();
        boolean ccw = type.isCounterClockwise();

        if (side == facing && fwd)
        {
            return edge == facing.getClockWise() || (top && edge == Direction.UP) || (bottom && edge == Direction.DOWN);
        }
        else if (side == facing.getCounterClockWise() && ccw)
        {
            return edge == facing.getOpposite() || (top && edge == Direction.UP) || (bottom && edge == Direction.DOWN);
        }
        else if (side == facing.getOpposite())
        {
            return ccw || edge != facing.getCounterClockWise();
        }
        else if (side == facing.getClockWise())
        {
            return fwd || edge != facing;
        }
        else if (side == Direction.UP)
        {
            if (edge == facing.getOpposite() || edge == facing.getClockWise())
            {
                return true;
            }
            return top && ((fwd && edge == facing) || (ccw && edge == facing.getCounterClockWise()));
        }
        else if (side == Direction.DOWN)
        {
            if (edge == facing.getOpposite() || edge == facing.getClockWise())
            {
                return true;
            }
            return bottom && ((fwd && edge == facing) || (ccw && edge == facing.getCounterClockWise()));
        }
        return false;
    }
}
