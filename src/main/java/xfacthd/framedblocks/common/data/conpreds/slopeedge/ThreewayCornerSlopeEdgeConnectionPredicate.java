package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class ThreewayCornerSlopeEdgeConnectionPredicate implements ConnectionPredicate
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
        boolean top = state.getValue(FramedProperties.TOP);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();

        if (side == dir)
        {
            return edge == bottom || edge == dirTwo;
        }
        if (side == dirTwo)
        {
            return edge == bottom || edge == dir;
        }
        if (side == bottom)
        {
            return edge == dir || edge == dirTwo;
        }
        if (side == dir.getOpposite())
        {
            return edge != dirTwo.getOpposite();
        }
        if (side == dirTwo.getOpposite())
        {
            return edge != dir.getOpposite();
        }
        if (side == bottom.getOpposite())
        {
            return edge == dir || edge == dirTwo;
        }
        return false;
    }
}
