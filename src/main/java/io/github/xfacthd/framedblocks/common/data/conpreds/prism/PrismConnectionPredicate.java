package io.github.xfacthd.framedblocks.common.data.conpreds.prism;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class PrismConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);

        if (side == dirAxis.direction().getOpposite()) {
            return true;
        }
        if (side.getAxis() == dirAxis.axis()) {
            return edge == dirAxis.direction().getOpposite();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        Direction facing = dirAxis.direction();
        Direction.Axis axis = dirAxis.axis();

        if (side == facing || side.getAxis() == facing.getClockWise(axis).getAxis()) {
            return edge.getAxis() == axis;
        }
        return false;
    }
}
