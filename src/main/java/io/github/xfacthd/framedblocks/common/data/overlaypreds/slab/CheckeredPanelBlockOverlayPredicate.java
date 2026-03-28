package io.github.xfacthd.framedblocks.common.data.overlaypreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class CheckeredPanelBlockOverlayPredicate implements BlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        return side.getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        Direction.Axis axis = state.getValue(FramedProperties.FACING_HOR).getAxis();
        return side.getAxis() == axis ? !unaligned : !nullCullFace;
    }
}
