package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

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
            if (DirUtils.isY(side))
            {
                return edge == facing || edge == dirTwo;
            }
            else if (side == facing || side == dirTwo)
            {
                return edge != null && DirUtils.isY(edge);
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
