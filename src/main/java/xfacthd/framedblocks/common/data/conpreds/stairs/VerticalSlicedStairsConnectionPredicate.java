package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalSlicedStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean right = state.getValue(PropertyHolder.RIGHT);

        if (side == facing)
        {
            return switch (type)
            {
                case VERTICAL, TOP_CCW, BOTTOM_CCW -> right || (edge != null && edge.getAxis() == facing.getClockWise().getAxis());
                case TOP_FWD, TOP_BOTH -> edge == facing.getCounterClockWise() || (right && edge == Direction.DOWN);
                case BOTTOM_FWD, BOTTOM_BOTH -> edge == facing.getCounterClockWise() || (right && edge == Direction.UP);
            };
        }
        if (side == facing.getCounterClockWise())
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, BOTTOM_FWD -> !right || (edge != null && edge.getAxis() == facing.getAxis());
                case TOP_CCW, TOP_BOTH -> edge == facing || (!right && edge == Direction.DOWN);
                case BOTTOM_CCW, BOTTOM_BOTH -> edge == facing || (!right && edge == Direction.UP);
            };
        }
        if (side == Direction.UP)
        {
            if (!right && (!type.isTop() || !type.isCounterClockwise()) && edge == facing.getCounterClockWise())
            {
                return true;
            }
            if (right && (!type.isTop() || !type.isForward()) && edge == facing)
            {
                return true;
            }
            return false;
        }
        if (side == Direction.DOWN)
        {
            if (!right && (!type.isBottom() || !type.isCounterClockwise()) && edge == facing.getCounterClockWise())
            {
                return true;
            }
            if (right && (!type.isBottom() || !type.isForward()) && edge == facing)
            {
                return true;
            }
            return false;
        }
        if (side == facing.getOpposite())
        {
            return edge == facing.getCounterClockWise() && !type.isCounterClockwise();
        }
        if (side == facing.getClockWise())
        {
            return edge == facing && !type.isForward();
        }
        return false;
    }
}
