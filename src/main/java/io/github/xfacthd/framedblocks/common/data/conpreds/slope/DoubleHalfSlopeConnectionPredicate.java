package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleHalfSlopeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? facing.getClockWise() : facing.getCounterClockWise();

        if (side.getAxis() == facing.getAxis() || DirUtils.isY(side))
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
