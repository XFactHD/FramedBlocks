package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class DividedSlopeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);

        if (type == SlopeType.HORIZONTAL)
        {
            Direction dirTwo = facing.getCounterClockWise();
            if (Utils.isY(side))
            {
                return edge == facing || edge == dirTwo;
            }
            else if (side == facing || side == dirTwo)
            {
                return edge != null && Utils.isY(edge);
            }
        }
        else
        {
            Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
            if (side.getAxis() == facing.getClockWise().getAxis())
            {
                return edge == facing || edge == dirTwo;
            }
            else if (side == facing || side == dirTwo)
            {
                return edge != null && edge.getAxis() == facing.getClockWise().getAxis();
            }
        }
        return false;
    }
}
