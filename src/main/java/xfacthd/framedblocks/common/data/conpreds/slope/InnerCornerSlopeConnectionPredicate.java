package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class InnerCornerSlopeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        Direction base;
        Direction dirOne;
        Direction dirTwo;
        switch (type)
        {
            case BOTTOM ->
            {
                base = Direction.DOWN;
                dirOne = facing;
                dirTwo = facing.getCounterClockWise();
            }
            case TOP ->
            {
                base = Direction.UP;
                dirOne = facing;
                dirTwo = facing.getCounterClockWise();
            }
            default ->
            {
                base = facing;
                dirOne = type.isTop() ? Direction.UP : Direction.DOWN;
                dirTwo = type.isRight() ? facing.getClockWise() : facing.getCounterClockWise();
            }
        }

        if (side == base || side == dirOne || side == dirTwo)
        {
            return true;
        }
        else if (side == dirOne.getOpposite())
        {
            return edge == base || edge == dirTwo;
        }
        else if (side == dirTwo.getOpposite())
        {
            return edge == base || edge == dirOne;
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        Direction base;
        Direction dirOne;
        Direction dirTwo;
        switch (type)
        {
            case BOTTOM ->
            {
                base = Direction.DOWN;
                dirOne = facing;
                dirTwo = facing.getCounterClockWise();
            }
            case TOP ->
            {
                base = Direction.UP;
                dirOne = facing;
                dirTwo = facing.getCounterClockWise();
            }
            default ->
            {
                base = facing;
                dirOne = type.isTop() ? Direction.UP : Direction.DOWN;
                dirTwo = type.isRight() ? facing.getClockWise() : facing.getCounterClockWise();
            }
        }

        if (side == dirOne.getOpposite())
        {
            return edge == dirTwo.getOpposite();
        }
        else if (side == dirTwo.getOpposite())
        {
            return edge == dirOne.getOpposite();
        }
        else if (side == base.getOpposite())
        {
            return edge == dirOne.getOpposite() || edge == dirTwo.getOpposite();
        }

        return false;
    }
}
