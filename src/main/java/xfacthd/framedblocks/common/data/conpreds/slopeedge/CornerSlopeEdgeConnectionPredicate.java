package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class CornerSlopeEdgeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        boolean alt = state.getValue(PropertyHolder.ALT_TYPE);

        Direction bottom;
        Direction backOne;
        Direction backTwo;
        if (type.isHorizontal())
        {
            bottom = dir;
            backOne = type.isTop() ? Direction.UP : Direction.DOWN;
            backTwo = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
        }
        else
        {
            bottom = type.isTop() ? Direction.UP : Direction.DOWN;
            backOne = dir;
            backTwo = dir.getCounterClockWise();
        }

        if (side == bottom)
        {
            return edge == backOne || edge == backTwo;
        }
        else if (side == backOne)
        {
            return edge == backTwo || (!alt && edge == bottom);
        }
        else if (side == backTwo)
        {
            return edge == backOne || (!alt && edge == bottom);
        }
        else if (side == backOne.getOpposite())
        {
            return edge == backTwo || (!alt && edge == bottom);
        }
        else if (side == backTwo.getOpposite())
        {
            return edge == backOne || (!alt && edge == bottom);
        }
        else if (side == bottom.getOpposite())
        {
            return edge == backOne || edge == backTwo;
        }
        return false;
    }
}
