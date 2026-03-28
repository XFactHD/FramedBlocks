package io.github.xfacthd.framedblocks.common.data.facepreds.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class SlopeSlabFullFacePredicate implements FullFacePredicate {
    public static final SlopeSlabFullFacePredicate INSTANCE = new SlopeSlabFullFacePredicate();

    private SlopeSlabFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (state.getValue(FramedProperties.TOP)) {
            return topHalf && side == Direction.UP;
        } else {
            return !topHalf && side == Direction.DOWN;
        }
    }
}
