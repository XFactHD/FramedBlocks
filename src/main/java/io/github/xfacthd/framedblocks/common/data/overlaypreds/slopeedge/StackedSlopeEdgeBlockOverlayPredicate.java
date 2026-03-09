package io.github.xfacthd.framedblocks.common.data.overlaypreds.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class StackedSlopeEdgeBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return secondPart;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = switch (state.getValue(PropertyHolder.SLOPE_TYPE))
        {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> facing.getCounterClockWise();
            case TOP -> Direction.UP;
        };
        if (!secondPart)
        {
            if (!nullCullFace)
            {
                return !unaligned || side.getAxis() == facing.getAxis() || side.getAxis() == dirTwo.getAxis();
            }
            return false;
        }
        return side != facing && side != dirTwo && edge != facing && edge != dirTwo;
    }
}
