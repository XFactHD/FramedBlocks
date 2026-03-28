package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.AlwaysSolidBlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class SlopedStairsBlockOverlayPredicate extends AlwaysSolidBlockOverlayPredicate {
    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dirTwo.getOpposite()) {
            return edge != facing && edge != facing.getCounterClockWise();
        }
        if (side == facing.getOpposite() || side == facing.getClockWise()) {
            return edge != dirTwo;
        }
        return true;
    }
}
