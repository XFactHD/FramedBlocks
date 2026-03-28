package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class VerticalHalfSlopeConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean up = state.getValue(FramedProperties.TOP);
        Direction dirTwo = up ? Direction.UP : Direction.DOWN;

        if (side == dirTwo) {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        if (side == facing || side == facing.getCounterClockWise()) {
            return edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean up = state.getValue(FramedProperties.TOP);
        Direction dirTwo = up ? Direction.UP : Direction.DOWN;

        if (side == facing) {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        if (side == facing.getCounterClockWise()) {
            return edge.getAxis() == facing.getAxis();
        }
        if (side == facing.getOpposite() || side == facing.getClockWise()) {
            return edge == dirTwo;
        }
        if (side == dirTwo.getOpposite()) {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        return false;
    }
}
