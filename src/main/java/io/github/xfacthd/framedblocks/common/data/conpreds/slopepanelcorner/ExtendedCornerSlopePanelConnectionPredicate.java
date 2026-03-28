package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class ExtendedCornerSlopePanelConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dirTwo) {
            return true;
        }
        if (side == dir) {
            return edge == dir.getCounterClockWise() || edge == dirTwo;
        }
        if (side == dir.getCounterClockWise()) {
            return edge == dir || edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dir || side == dir.getCounterClockWise()) {
            return edge == dirTwo.getOpposite();
        }
        if (side == dirTwo.getOpposite()) {
            return true;
        }
        if (side == dir.getOpposite()) {
            return edge == dir.getCounterClockWise();
        }
        if (side == dir.getClockWise()) {
            return edge == dir;
        }
        return false;
    }
}
