package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class VerticalSlicedStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dirTwo || (side.getAxis() != dirTwo.getAxis() && edge == dirTwo))
        {
            return true;
        }
        else if (side == dir && edge == dirTwo.getOpposite())
        {
            return true;
        }
        else if (side == dirTwo.getOpposite())
        {
            return edge == dir;
        }
        return false;
    }
}
