package io.github.xfacthd.framedblocks.common.data.overlaypreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.overlay.AlwaysSolidBlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedPartialBoardBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class InnerCornerBoardBlockOverlayPredicate extends AlwaysSolidBlockOverlayPredicate {
    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction dirOne = cmpDir.orientation();
        Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
        if (side == dirOne.getOpposite()) {
            return edge != dirTwo;
        }
        if (side == dirTwo.getOpposite()) {
            return edge != dirOne;
        }
        return true;
    }
}
