package io.github.xfacthd.framedblocks.common.data.conpreds.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlopeEdgePanelConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction backEdge = state.getValue(PropertyHolder.ROTATION).withFacing(dir).getOpposite();
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir) {
            return !front;
        }
        if (side == dir.getOpposite()) {
            return front && edge == backEdge;
        }
        if (side == backEdge) {
            return front ? edge == dir.getOpposite() : edge == dir;
        }
        if (side != backEdge.getOpposite()) {
            return !front && edge == dir;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction backEdge = state.getValue(PropertyHolder.ROTATION).withFacing(dir).getOpposite();
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir) {
            return front;
        }
        if (side == dir.getOpposite()) {
            if (edge == backEdge) {
                return !front;
            }
            return edge.getAxis() != backEdge.getAxis();
        }
        if (side.getAxis() == backEdge.getAxis()) {
            return edge.getAxis() != dir.getAxis();
        }
        if (edge == dir.getOpposite()) {
            return front;
        }
        return edge == backEdge;
    }
}
