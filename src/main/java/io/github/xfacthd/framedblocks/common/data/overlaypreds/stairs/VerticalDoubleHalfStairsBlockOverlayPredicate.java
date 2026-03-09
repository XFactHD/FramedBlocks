package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalDoubleHalfStairsBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        Direction innerFace = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        return side == innerFace;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction innerFace = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        return (!nullCullFace || side == innerFace) && (!unaligned || edge == innerFace);
    }
}
