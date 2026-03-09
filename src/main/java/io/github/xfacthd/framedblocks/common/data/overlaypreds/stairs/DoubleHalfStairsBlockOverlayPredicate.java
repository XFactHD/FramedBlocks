package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DoubleHalfStairsBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction innerFace = state.getValue(PropertyHolder.RIGHT) ? facing.getCounterClockWise() : facing.getClockWise();
        return side == innerFace;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction innerFace = state.getValue(PropertyHolder.RIGHT) ? facing.getCounterClockWise() : facing.getClockWise();
        return (!nullCullFace || side == innerFace) && (!unaligned || edge == innerFace);
    }
}
