package xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class LargeInnerCornerSlopePanelConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dir.getOpposite() || side == dir.getClockWise())
        {
            return true;
        }
        else if (side == dirTwo)
        {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        else if (side == dir)
        {
            return edge == dir.getClockWise();
        }
        else if (side == dir.getCounterClockWise())
        {
            return edge == dir.getOpposite();
        }
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
        else if (side == dir || side == dir.getCounterClockWise() || side == dirTwo.getOpposite())
        {
            return edge == dir || edge == dir.getCounterClockWise() || edge == dirTwo;
        }
        return false;
    }
}
