package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalHalfStairsBlockOverlayPredicate extends AbstractVerticalStairsBlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        return supportsEdgeVertical(state, side, edge, nullCullFace);
    }
}
