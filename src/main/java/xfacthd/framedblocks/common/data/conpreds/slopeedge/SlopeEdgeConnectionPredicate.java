package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class SlopeEdgeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (state.getValue(PropertyHolder.ALT_TYPE)) return false;

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction dirTwo = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
            case TOP -> Direction.UP;
        };
        return (side == dir && edge == dirTwo) || (side == dirTwo && edge == dir);
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        boolean alt = state.getValue(PropertyHolder.ALT_TYPE);

        if (type == SlopeType.HORIZONTAL)
        {
            if (Utils.isY(side))
            {
                return !alt && (edge == dir || edge == dir.getCounterClockWise());
            }
            if (Utils.isY(edge))
            {
                return !Utils.isY(side);
            }
            if (side == dir.getOpposite())
            {
                return edge == dir.getCounterClockWise();
            }
            if (side == dir.getClockWise())
            {
                return edge == dir;
            }
        }
        else
        {
            Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
            if (side == dir.getClockWise() || side == dir.getCounterClockWise())
            {
                return !alt && (edge == dir || edge == dirTwo);
            }
            if (side == dir || side == dirTwo)
            {
                return edge.getAxis() == dir.getClockWise().getAxis();
            }
            if (side == dirTwo.getOpposite())
            {
                return edge.getAxis() == dir.getClockWise().getAxis() || (alt ? edge == dir.getOpposite() : edge == dir);
            }
            if (side == dir.getOpposite())
            {
                return edge.getAxis() == dir.getClockWise().getAxis() || (alt ? edge == dirTwo.getOpposite() : edge == dirTwo);
            }
        }

        return false;
    }
}
