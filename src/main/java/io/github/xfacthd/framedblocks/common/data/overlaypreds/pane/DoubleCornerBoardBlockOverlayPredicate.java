package io.github.xfacthd.framedblocks.common.data.overlaypreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedPartialBoardBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DoubleCornerBoardBlockOverlayPredicate implements BlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return side == cmpDir.direction().getOpposite();
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction dirOne = cmpDir.orientation();
        Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
        if (secondPart) {
            return side != dirOne && side != dirTwo && edge != dirOne && edge != dirTwo;
        } else if (nullCullFace) {
            Direction dirOneOpp = dirOne.getOpposite();
            Direction dirTwoOpp = dirTwo.getOpposite();
            return side != dirOneOpp && side != dirTwoOpp && edge != dirOneOpp && edge != dirTwoOpp;
        }
        return true;
    }
}
