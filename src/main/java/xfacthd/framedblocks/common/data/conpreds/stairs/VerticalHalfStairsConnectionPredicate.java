package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public final class VerticalHalfStairsConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise())
        {
            return edge == dirTwo;
        }
        else if (side == dirTwo)
        {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side.getAxis() == facing.getAxis())
        {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        else if (side.getAxis() == facing.getClockWise().getAxis())
        {
            return edge.getAxis() == facing.getAxis();
        }
        else if (side == dirTwo)
        {
            return edge == facing.getOpposite() || edge == facing.getClockWise();
        }
        else if (side == dirTwo.getOpposite())
        {
            return true;
        }
        return false;
    }
}
