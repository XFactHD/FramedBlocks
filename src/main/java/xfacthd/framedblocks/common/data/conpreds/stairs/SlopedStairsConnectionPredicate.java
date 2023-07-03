package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public final class SlopedStairsConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo || side == facing || side == facing.getCounterClockWise())
        {
            return true;
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return edge == dirTwo;
        }
        else if (side == dirTwo.getOpposite())
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
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing.getOpposite())
        {
            return edge == dirTwo || edge == facing.getClockWise();
        }
        else if (side == facing.getClockWise())
        {
            return edge == dirTwo || edge == facing.getOpposite();
        }
        else if (side == dirTwo)
        {
            return edge == facing.getOpposite() || edge == facing.getClockWise();
        }
        return false;
    }
}
