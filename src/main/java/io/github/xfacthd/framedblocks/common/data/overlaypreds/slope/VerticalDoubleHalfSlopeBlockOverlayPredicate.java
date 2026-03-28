package io.github.xfacthd.framedblocks.common.data.overlaypreds.slope;

import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalDoubleHalfSlopeBlockOverlayPredicate implements BlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        return DirUtils.isY(side);
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        return DirUtils.isY(side) ? !unaligned : !nullCullFace;
    }
}
