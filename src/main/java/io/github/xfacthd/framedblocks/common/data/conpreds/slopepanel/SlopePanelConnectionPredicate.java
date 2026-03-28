package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlopePanelConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean front = state.getValue(PropertyHolder.FRONT);
        Direction dir = front ? facing.getOpposite() : facing;
        Direction rotDir = rot.withFacing(facing);

        if (!front && side == facing) {
            return true;
        }
        if (side == rotDir.getOpposite()) {
            return edge == dir;
        }
        if (!front && side.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis()) {
            return edge == facing;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == rotDir.getOpposite() || side == facing.getOpposite() || side == rotDir) {
            return front ? (edge != rotDir.getOpposite() && edge != facing.getOpposite()) : (edge != rotDir && edge != facing);
        }
        if (side.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis()) {
            return edge == rotDir.getOpposite();
        }
        if (side == facing && front) {
            return true;
        }
        return false;
    }
}
