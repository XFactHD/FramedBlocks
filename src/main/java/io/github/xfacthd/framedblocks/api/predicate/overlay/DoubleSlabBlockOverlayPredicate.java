package io.github.xfacthd.framedblocks.api.predicate.overlay;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

final class DoubleSlabBlockOverlayPredicate implements BlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        return switch (side) {
            case UP -> secondPart;
            case DOWN -> !secondPart;
            default -> true;
        };
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        if (!DirUtils.isY(side)) {
            return switch (edge) {
                case UP -> secondPart;
                case DOWN -> !secondPart;
                default -> true;
            };
        }
        return true;
    }
}
