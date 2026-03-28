package io.github.xfacthd.framedblocks.common.data.facepreds.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class ElevatedSlopeSlabFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing) {
            return true;
        }

        boolean top = state.getValue(FramedProperties.TOP);
        if (top) {
            return side == Direction.UP;
        } else {
            return side == Direction.DOWN;
        }
    }
}
