package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public sealed class ThreewayCornerConnectionPredicate implements ConnectionPredicate
    permits PrismCornerConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing)
        {
            return edge == facing.getCounterClockWise() || edge == dirTwo;
        }
        else if (side == facing.getCounterClockWise())
        {
            return edge == facing || edge == dirTwo;
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
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing.getOpposite() || side == facing.getClockWise() || side == dirTwo)
        {
            return edge == facing || edge == facing.getCounterClockWise() || edge == dirTwo.getOpposite();
        }
        return false;
    }
}
