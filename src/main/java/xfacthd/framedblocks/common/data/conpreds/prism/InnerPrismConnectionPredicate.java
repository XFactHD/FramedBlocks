package xfacthd.framedblocks.common.data.conpreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public final class InnerPrismConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        Direction facing = dirAxis.direction();
        Direction.Axis axis = dirAxis.axis();

        if (side == facing.getOpposite() || side.getAxis() == facing.getClockWise(axis).getAxis())
        {
            return true;
        }
        else if (side.getAxis() == axis)
        {
            return edge != facing;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return side == dirAxis.direction() && edge.getAxis() == dirAxis.axis();
    }
}
