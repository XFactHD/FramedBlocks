package xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class SmallInnerCornerSlopePanelConnectionPredicate implements ConnectionPredicate
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
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dirTwo)
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        else if (side == dir.getClockWise())
        {
            return edge != dir.getOpposite();
        }
        else if (side == dir.getOpposite())
        {
            return edge != dir.getClockWise();
        }
        else if (side == dir || side == dir.getCounterClockWise() || side == dirTwo.getOpposite())
        {
            return edge == dir || edge == dir.getCounterClockWise() || edge == dirTwo;
        }
        return false;
    }
}
