package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class ExtendedSlopePanelConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);

        if (side == facing || side == rotDir.getOpposite()) {
            return true;
        }
        if (side == rotDir) {
            return edge == facing;
        }
        if (side.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis()) {
            return edge == facing || edge == rotDir.getOpposite();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);

        if (side == facing.getOpposite() || side == rotDir) {
            return edge != rotDir.getOpposite();
        }
        if (side.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis()) {
            return edge == rotDir;
        }
        return false;
    }
}
