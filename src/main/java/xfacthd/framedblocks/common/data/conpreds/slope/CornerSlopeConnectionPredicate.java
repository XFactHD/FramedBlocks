package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class CornerSlopeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction fullFace = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            default -> facing;
        };

        if (side == fullFace)
        {
            return true;
        }

        Direction dirTwo;
        Direction dirThree;
        if (type.isHorizontal())
        {
            dirTwo = type.isRight() ? facing.getClockWise() : facing.getCounterClockWise();
            dirThree = type.isTop() ? Direction.UP : Direction.DOWN;
        }
        else
        {
            dirTwo = facing;
            dirThree = facing.getCounterClockWise();
        }

        if (side == dirTwo)
        {
            return edge == fullFace || edge == dirThree;
        }
        else if (side == dirThree)
        {
            return edge == fullFace || edge == dirTwo;
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            Direction dirTwo = type.isRight() ? facing.getCounterClockWise() : facing.getClockWise();
            Direction dirThree = type.isTop() ? Direction.DOWN : Direction.UP;

            if (side == dirTwo)
            {
                return edge == dirThree.getOpposite();
            }
            else if (side == dirThree)
            {
                return edge == dirTwo.getOpposite();
            }
            else if (side == facing.getOpposite())
            {
                return edge == dirTwo.getOpposite() || edge == dirThree.getOpposite();
            }
        }
        else
        {
            if (side == facing.getOpposite())
            {
                return edge == facing.getCounterClockWise();
            }
            else if (side == facing.getClockWise())
            {
                return edge == facing;
            }
            else if ((!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN))
            {
                return edge == facing || edge == facing.getCounterClockWise();
            }
        }

        return false;
    }
}
