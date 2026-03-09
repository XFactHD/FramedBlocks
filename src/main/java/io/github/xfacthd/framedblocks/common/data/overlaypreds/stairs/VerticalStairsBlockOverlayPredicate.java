package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalStairsBlockOverlayPredicate extends AbstractVerticalStairsBlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        return switch (state.getValue(PropertyHolder.STAIRS_TYPE))
        {
            case VERTICAL -> supportsEdgeVertical(state, side, edge, nullCullFace);
            case TOP_FWD -> supportsEdgeTopForward(state, side, edge, nullCullFace);
            case TOP_CCW -> supportsEdgeTopCounterClockwise(state, side, edge, nullCullFace);
            case TOP_BOTH -> supportsEdgeTopBoth(state, side, edge, nullCullFace, unaligned);
            case BOTTOM_FWD -> supportsEdgeBottomForward(state, side, edge, nullCullFace);
            case BOTTOM_CCW -> supportsEdgeBottomCounterClockwise(state, side, edge, nullCullFace);
            case BOTTOM_BOTH -> supportsEdgeBottomBoth(state, side, edge, nullCullFace, unaligned);
        };
    }
}
