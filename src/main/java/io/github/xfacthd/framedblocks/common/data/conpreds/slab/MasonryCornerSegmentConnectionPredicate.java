package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class MasonryCornerSegmentConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        if (side == bottom) {
            return edge == dir.getOpposite();
        }
        if (side == bottom.getOpposite()) {
            return edge == dir.getClockWise();
        }
        if (side == dir.getOpposite()) {
            return edge == bottom || edge == side.getCounterClockWise();
        }
        if (side == dir.getClockWise()) {
            return edge == bottom.getOpposite() || edge == side.getClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        if (side == bottom) {
            return edge != dir.getOpposite();
        }
        if (side == bottom.getOpposite()) {
            return edge != dir.getClockWise();
        }
        if (side == dir || side == dir.getCounterClockWise()) {
            return true;
        }
        if (side == dir.getOpposite()) {
            return edge == bottom.getOpposite() || edge == side.getClockWise();
        }
        if (side == dir.getClockWise()) {
            return edge == bottom || edge == side.getCounterClockWise();
        }
        return false;
    }
}
