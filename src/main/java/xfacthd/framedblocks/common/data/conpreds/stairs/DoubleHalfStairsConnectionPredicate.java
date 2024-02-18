package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class DoubleHalfStairsConnectionPredicate extends NonDetailedConnectionPredicate
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
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        Direction dirThree = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dir || side == dirTwo)
        {
            return edge == dirThree;
        }
        else if (side == dirThree)
        {
            return edge == dir || edge == dirTwo;
        }
        return false;
    }
}
