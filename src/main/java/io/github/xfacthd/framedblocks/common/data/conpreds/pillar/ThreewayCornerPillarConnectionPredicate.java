package io.github.xfacthd.framedblocks.common.data.conpreds.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class ThreewayCornerPillarConnectionPredicate implements ConnectionPredicate {
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
        if (side == dirTwo) {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
            return true;
        }
        if (side == dir) {
            return (!top && edge == Direction.UP) || (top && edge == Direction.DOWN) || edge == dir.getClockWise();
        }
        if (side == dir.getCounterClockWise()) {
            return (!top && edge == Direction.UP) || (top && edge == Direction.DOWN) || edge == dir.getOpposite();
        }
        if (side == dir.getOpposite() || side == dir.getClockWise()) {
            return true;
        }
        return false;
    }
}
