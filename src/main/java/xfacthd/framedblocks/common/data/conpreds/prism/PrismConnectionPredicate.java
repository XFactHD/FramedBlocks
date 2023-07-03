package xfacthd.framedblocks.common.data.conpreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public final class PrismConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);

        if (side == dirAxis.direction())
        {
            return true;
        }
        else if (side.getAxis() == dirAxis.axis())
        {
            return edge == dirAxis.direction();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        Direction facing = dirAxis.direction();
        Direction.Axis axis = dirAxis.axis();

        if (side == facing.getOpposite() || side.getAxis() == facing.getClockWise(axis).getAxis())
        {
            return edge.getAxis() == axis;
        }
        return false;
    }
}
