package io.github.xfacthd.framedblocks.common.data.facepreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalSlopedStairsFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing) {
            return true;
        }

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.getOpposite().withFacing(facing);
        Direction perpRotDir = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);
        return side == rotDir || side == perpRotDir;
    }
}
