package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public sealed class InnerThreewayCornerConnectionPredicate implements ConnectionPredicate
    permits InnerPrismCornerConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise() || side == dirTwo)
        {
            return true;
        }
        else if (side == dirTwo.getOpposite())
        {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        else if (side == facing.getClockWise())
        {
            return edge == facing || edge == dirTwo;
        }
        else if (side == facing.getOpposite())
        {
            return edge == facing.getCounterClockWise() || edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing.getOpposite() || side == facing.getClockWise() || side == dirTwo)
        {
            return edge == facing.getOpposite() || edge == facing.getClockWise() || edge == dirTwo;
        }
        return false;
    }
}
