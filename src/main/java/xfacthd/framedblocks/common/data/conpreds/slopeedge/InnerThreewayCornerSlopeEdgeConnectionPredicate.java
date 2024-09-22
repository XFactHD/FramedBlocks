package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class InnerThreewayCornerSlopeEdgeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (state.getValue(PropertyHolder.ALT_TYPE)) return false;

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
            return edge == bottom.getOpposite() || edge == dirTwo.getOpposite();
        }
        if (side == dirTwo)
        {
            return edge == bottom.getOpposite() || edge == dir.getOpposite();
        }
        if (side == bottom)
        {
            return edge == dir.getOpposite() || edge == dirTwo.getOpposite();
        }
        if (side == dir.getOpposite() || side == dirTwo.getOpposite() || side == bottom.getOpposite())
        {
            return true;
        }
        return false;
    }
}
