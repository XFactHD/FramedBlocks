package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LargeInnerPrismSlopePanelCornerWallConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction backDirOne = rot.withFacing(dir);
        Direction backDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side.getAxis() == dir.getAxis()) {
            return edge == backDirOne || edge == backDirTwo;
        }
        if (side == backDirOne || side == backDirTwo) {
            return true;
        }
        if (side == backDirOne.getOpposite()) {
            return edge == backDirTwo;
        }
        if (side == backDirTwo.getOpposite()) {
            return edge == backDirOne;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction backDirOne = rot.withFacing(dir);
        Direction backDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == dir) {
            return edge == backDirOne.getOpposite() || edge == backDirTwo.getOpposite();
        }
        if (side == backDirOne.getOpposite() || side == backDirTwo.getOpposite()) {
            return edge.getAxis() == dir.getAxis();
        }
        return false;
    }
}
