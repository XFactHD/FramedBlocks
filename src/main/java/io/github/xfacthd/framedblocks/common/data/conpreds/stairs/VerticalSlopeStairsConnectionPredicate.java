package io.github.xfacthd.framedblocks.common.data.conpreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class VerticalSlopeStairsConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (side == facing || side == rotDir.getOpposite() || side == perpRotDir.getOpposite()) {
            return true;
        }
        if (side == facing.getOpposite()) {
            return edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite();
        }
        if (side == rotDir || side == perpRotDir) {
            return edge == facing;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (side == rotDir) {
            return edge == facing.getOpposite() || edge == perpRotDir;
        }
        if (side == perpRotDir) {
            return edge == facing.getOpposite() || edge == rotDir;
        }
        if (side == facing.getOpposite()) {
            return edge == rotDir || edge == perpRotDir;
        }
        return false;
    }
}
