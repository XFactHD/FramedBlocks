package io.github.xfacthd.framedblocks.common.data.conpreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlopedStairsConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo || side == facing || side == facing.getCounterClockWise()) {
            return true;
        }
        if (side == facing.getOpposite() || side == facing.getClockWise()) {
            return edge == dirTwo;
        }
        if (side == dirTwo.getOpposite()) {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing.getOpposite()) {
            return edge == dirTwo || edge == facing.getClockWise();
        }
        if (side == facing.getClockWise()) {
            return edge == dirTwo || edge == facing.getOpposite();
        }
        if (side == dirTwo) {
            return edge == facing.getOpposite() || edge == facing.getClockWise();
        }
        return false;
    }
}
