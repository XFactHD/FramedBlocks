package xfacthd.framedblocks.common.data.conpreds.pillar;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;

public final class DoubleThreewayCornerPillarConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (edge == null)
        {
            return false;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dir)
        {
            return edge == dir.getCounterClockWise() || edge == dirTwo;
        }
        else if (side == dir.getCounterClockWise())
        {
            return edge == dir || edge == dirTwo;
        }
        else if (side == dir.getOpposite())
        {
            return edge == dir.getClockWise() || edge == dirTwo.getOpposite();
        }
        else if (side == dir.getClockWise())
        {
            return edge == dir.getOpposite() || edge == dirTwo.getOpposite();
        }
        else if (side == dirTwo)
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        else if (side == dirTwo.getOpposite())
        {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        return false;
    }
}
