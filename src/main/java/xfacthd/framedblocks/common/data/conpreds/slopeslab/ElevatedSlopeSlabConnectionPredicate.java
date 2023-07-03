package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public final class ElevatedSlopeSlabConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == dirTwo)
        {
            return true;
        }
        else if (side.getAxis() == facing.getClockWise().getAxis())
        {
            return edge == facing || edge == dirTwo;
        }
        else if (side == facing.getOpposite())
        {
            return edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side.getAxis() == facing.getClockWise().getAxis())
        {
            return edge == facing.getOpposite();
        }
        else if (side == facing.getOpposite() || side == dirTwo)
        {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        return false;
    }
}
