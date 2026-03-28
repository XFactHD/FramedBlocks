package io.github.xfacthd.framedblocks.common.data.conpreds.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlopeSlabConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = topHalf ? Direction.UP : Direction.DOWN;
        boolean solidFace = top == topHalf;

        if (solidFace && side == dirTwo) {
            return true;
        }
        if (side == facing) {
            return edge == dirTwo;
        }
        if (solidFace && side.getAxis() == facing.getClockWise().getAxis()) {
            return edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing) {
            return edge.getAxis() == facing.getClockWise().getAxis();
        }
        if (top != topHalf && side == dirTwo.getOpposite()) {
            return true;
        }
        if (side.getAxis() == facing.getClockWise().getAxis()) {
            return edge == facing;
        }
        if (side == facing.getOpposite() || side == dirTwo) {
            return (top == topHalf ? edge != facing.getOpposite() : edge != facing) && edge != dirTwo.getOpposite();
        }
        return false;
    }
}
