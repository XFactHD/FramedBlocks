package io.github.xfacthd.framedblocks.api.predicate.overlay;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

final class DoublePanelBlockOverlayPredicate implements BlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing) {
            return !secondPart;
        }
        if (side == facing.getOpposite()) {
            return secondPart;
        }
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side.getAxis() != facing.getAxis())
        {
            if (edge == facing) {
                return !secondPart;
            }
            if (edge == facing.getOpposite()) {
                return secondPart;
            }
            return true;
        }
        return true;
    }
}
