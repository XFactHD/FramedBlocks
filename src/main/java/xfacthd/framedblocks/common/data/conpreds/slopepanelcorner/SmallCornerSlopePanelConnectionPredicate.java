package xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class SmallCornerSlopePanelConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return (side == dir && edge == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && edge == dir);
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
        else if (side == dir || side == dir.getCounterClockWise())
        {
            return edge == dirTwo;
        }
        else if (side == dirTwo.getOpposite() || side == dir.getClockWise() || side == dir.getOpposite())
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        return false;
    }
}
