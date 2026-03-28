package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SmallInnerPrismSlopePanelCornerConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction botDir = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (side == dir || side == dir.getCounterClockWise() || side == botDir) {
            return true;
        }
        if (side == dir.getOpposite()) {
            return edge == dir.getCounterClockWise() || edge == botDir;
        }
        if (side == dir.getClockWise()) {
            return edge == dir || edge == botDir;
        }
        if (side == botDir.getOpposite()) {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction topDir = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        if (side == topDir) {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        if (side == dir.getOpposite() || side == dir.getClockWise()) {
            return edge == topDir;
        }
        return false;
    }
}
