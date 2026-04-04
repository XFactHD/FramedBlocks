package io.github.xfacthd.framedblocks.common.data.overlaypreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DividedBoardBlockOverlayPredicate implements BlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return side == cmpDir.direction().getOpposite();
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (secondPart) {
            Direction dir = cmpDir.orientation();
            return side != dir && edge != dir;
        } else {
            Direction oppDir = cmpDir.orientation().getOpposite();
            return side != oppDir && edge != oppDir;
        }
    }
}
