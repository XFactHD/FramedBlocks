package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class HalfStairsBlockOverlayPredicate extends AbstractStairsBlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        return supportsEdgeStraight(state, side, edge, nullCullFace);
    }

    @Override
    protected boolean isTopHalf(BlockState state) {
        return state.getValue(FramedProperties.TOP);
    }
}
