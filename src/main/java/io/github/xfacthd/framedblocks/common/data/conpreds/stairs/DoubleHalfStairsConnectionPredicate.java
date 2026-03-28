package io.github.xfacthd.framedblocks.common.data.conpreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleHalfStairsConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        if (edge == null) {
            return false;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        Direction dirThree = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dir || side == dirTwo) {
            return edge == dirThree;
        }
        if (side == dirThree) {
            return edge == dir || edge == dirTwo;
        }
        return false;
    }
}
