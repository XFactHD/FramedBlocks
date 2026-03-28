package io.github.xfacthd.framedblocks.common.data.conpreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class VerticalDoubleHalfStairsConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        if (edge == null) {
            return false;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dir || side == dir.getCounterClockWise()) {
            return edge == dirTwo;
        }
        if (side == dirTwo) {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        return false;
    }
}
