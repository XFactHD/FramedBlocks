package io.github.xfacthd.framedblocks.common.data.conpreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class VerticalSlicedSlopedStairsSlopeConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.getOpposite().withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);

        if (side == facing) {
            return edge != null;
        }
        if (side == facing.getOpposite()) {
            return edge == dirTwo || edge == dirThree;
        }
        if (side == dirTwo || side == dirThree) {
            return true;
        }
        if (side == dirTwo.getOpposite() || side == dirThree.getOpposite()) {
            return edge == facing;
        }
        return false;
    }
}
