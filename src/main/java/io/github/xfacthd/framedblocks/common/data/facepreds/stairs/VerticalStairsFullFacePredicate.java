package io.github.xfacthd.framedblocks.common.data.facepreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalStairsFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir) {
            return !type.isForward();
        }
        if (side == dir.getCounterClockWise()) {
            return !type.isCounterClockwise();
        }
        return false;
    }
}
