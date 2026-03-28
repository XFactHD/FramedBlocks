package io.github.xfacthd.framedblocks.common.data.facepreds.slopeslab;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class FlatDoubleSlopeSlabCornerFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        return topHalf ? side == Direction.UP : side == Direction.DOWN;
    }
}
