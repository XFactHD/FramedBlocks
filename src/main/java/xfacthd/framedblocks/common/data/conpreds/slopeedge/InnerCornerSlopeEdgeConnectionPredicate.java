package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class InnerCornerSlopeEdgeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (!state.getValue(PropertyHolder.ALT_TYPE))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            if (type.isHorizontal())
            {
                Direction backOne = type.isTop() ? Direction.UP : Direction.DOWN;
                Direction backTwo = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
                if (side == dir)
                {
                    return edge == backOne || edge == backTwo;
                }
                if (side == backOne || side == backTwo)
                {
                    return edge == dir;
                }
            }
            else
            {
                Direction bottomFace = type.isTop() ? Direction.UP : Direction.DOWN;
                if (side == bottomFace)
                {
                    return edge == dir || edge == dir.getCounterClockWise();
                }
                if (side == dir || side == dir.getCounterClockWise())
                {
                    return edge == bottomFace;
                }
            }
        }
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
            return edge == backOne.getOpposite() || edge == backTwo.getOpposite();
        }
        else if (side == backOne)
        {
            return edge == backTwo.getOpposite() || (!alt && edge == backTwo);
        }
        else if (side == backTwo)
        {
            return edge == backOne.getOpposite() || (!alt && edge == backOne);
        }
        else if (side == backOne.getOpposite())
        {
            return edge == backTwo.getOpposite() || (!alt && (edge == backTwo || edge == bottom));
        }
        else if (side == backTwo.getOpposite())
        {
            return edge == backOne.getOpposite() || (!alt && (edge == backOne || edge == bottom));
        }
        else return side == bottom.getOpposite();
    }
}
