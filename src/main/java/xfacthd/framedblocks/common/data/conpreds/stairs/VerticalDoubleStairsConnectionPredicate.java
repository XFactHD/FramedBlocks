package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalDoubleStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if (side == facing)
        {
            return switch (type)
            {
                case VERTICAL, TOP_CCW, BOTTOM_CCW -> true;
                case TOP_FWD, TOP_BOTH -> edge == facing.getCounterClockWise() || edge == Direction.DOWN;
                case BOTTOM_FWD, BOTTOM_BOTH -> edge == facing.getCounterClockWise() || edge == Direction.UP;
            };
        }
        if (side == facing.getCounterClockWise())
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, BOTTOM_FWD -> true;
                case TOP_CCW, TOP_BOTH -> edge == facing || edge == Direction.DOWN;
                case BOTTOM_CCW, BOTTOM_BOTH -> edge == facing || edge == Direction.UP;
            };
        }
        if (side == facing.getOpposite())
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, BOTTOM_FWD -> edge != null && edge.getAxis() == facing.getClockWise().getAxis();
                case TOP_CCW, TOP_BOTH -> edge == facing.getClockWise() || edge == Direction.UP;
                case BOTTOM_CCW, BOTTOM_BOTH -> edge == facing.getClockWise() || edge == Direction.DOWN;
            };
        }
        if (side == facing.getClockWise())
        {
            return switch (type)
            {
                case VERTICAL, TOP_CCW, BOTTOM_CCW -> edge != null && edge.getAxis() == facing.getAxis();
                case TOP_FWD, TOP_BOTH -> edge == facing.getOpposite() || edge == Direction.UP;
                case BOTTOM_FWD, BOTTOM_BOTH -> edge == facing.getOpposite() || edge == Direction.DOWN;
            };
        }
        if (side == Direction.UP)
        {
            return switch (type)
            {
                case VERTICAL, BOTTOM_FWD, BOTTOM_CCW, BOTTOM_BOTH -> edge == facing || edge == facing.getCounterClockWise();
                case TOP_CCW -> edge != null && edge.getAxis() == facing.getAxis();
                case TOP_FWD -> edge != null && edge.getAxis() == facing.getClockWise().getAxis();
                default -> edge == facing.getOpposite() || edge == facing.getClockWise();
            };
        }
        if (side == Direction.DOWN)
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, TOP_CCW, TOP_BOTH -> edge == facing || edge == facing.getCounterClockWise();
                case BOTTOM_CCW -> edge != null && edge.getAxis() == facing.getAxis();
                case BOTTOM_FWD -> edge != null && edge.getAxis() == facing.getClockWise().getAxis();
                default -> edge == facing.getOpposite() || edge == facing.getClockWise();
            };
        }
        return false;
    }
}
