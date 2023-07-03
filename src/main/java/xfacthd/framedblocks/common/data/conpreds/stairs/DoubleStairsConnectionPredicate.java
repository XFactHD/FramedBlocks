package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class DoubleStairsConnectionPredicate extends NonDetailedConnectionPredicate
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
        else if (side == dirTwo.getOpposite())
        {
            return edge != null && edge.getAxis() == facing.getAxis();
        }
        else if (side == facing.getOpposite())
        {
            return edge != null && Utils.isY(edge);
        }
        return false;
    }
}
