package io.github.xfacthd.framedblocks.common.data.conpreds.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleThreewayCornerPillarConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        if (edge == null) {
            return false;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dir) {
            return edge == dir.getCounterClockWise() || edge == dirTwo;
        }
        if (side == dir.getCounterClockWise()) {
            return edge == dir || edge == dirTwo;
        }
        if (side == dir.getOpposite()) {
            return edge == dir.getClockWise() || edge == dirTwo.getOpposite();
        }
        if (side == dir.getClockWise()) {
            return edge == dir.getOpposite() || edge == dirTwo.getOpposite();
        }
        if (side == dirTwo) {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        if (side == dirTwo.getOpposite()) {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        return false;
    }
}
