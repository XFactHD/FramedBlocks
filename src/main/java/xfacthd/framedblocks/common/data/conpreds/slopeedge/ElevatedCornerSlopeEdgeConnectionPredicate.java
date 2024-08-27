package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class ElevatedCornerSlopeEdgeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            if (side == dir)
            {
                return true;
            }
            Direction xBack = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            Direction yBack = type.isTop() ? Direction.UP : Direction.DOWN;
            if (side == xBack)
            {
                return edge == dir || edge == yBack;
            }
            if (side == yBack)
            {
                return edge == dir || edge == xBack;
            }
            if (side == xBack.getOpposite() || side == yBack.getOpposite())
            {
                return edge == dir;
            }
        }
        else
        {
            Direction bottom = type == CornerType.TOP ? Direction.UP : Direction.DOWN;
            if (side == bottom)
            {
                return true;
            }
            if (side == dir)
            {
                return edge == bottom || edge == dir.getCounterClockWise();
            }
            if (side == dir.getCounterClockWise())
            {
                return edge == bottom || edge == dir;
            }
            if (side == dir.getOpposite() || side == dir.getClockWise())
            {
                return edge == bottom;
            }
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        Direction top;
        Direction backOne;
        Direction backTwo;
        if (type.isHorizontal())
        {
            top = dir.getOpposite();
            backOne = type.isTop() ? Direction.UP : Direction.DOWN;
            backTwo = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
        }
        else
        {
            top = type == CornerType.TOP ? Direction.DOWN : Direction.UP;
            backOne = dir;
            backTwo = dir.getCounterClockWise();
        }

        if (side == top)
        {
            return true;
        }
        if (side == backOne)
        {
            return edge == backTwo.getOpposite() || edge == top;
        }
        if (side == backTwo)
        {
            return edge == backOne.getOpposite() || edge == top;
        }
        if (side == backOne.getOpposite())
        {
            return edge.getAxis() == backTwo.getAxis() || edge == top;
        }
        if (side == backTwo.getOpposite())
        {
            return edge.getAxis() == backOne.getAxis() || edge == top;
        }
        return false;
    }
}
