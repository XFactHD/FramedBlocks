package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class DoubleHalfSlopeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? facing.getClockWise() : facing.getCounterClockWise();

        if (side.getAxis() == facing.getAxis() || Utils.isY(side))
        {
            return edge == dirTwo;
        }
        else if (side == dirTwo)
        {
            return edge != null;
        }
        return false;
    }
}
