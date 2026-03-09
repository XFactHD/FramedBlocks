package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

public final class StairsBlockOverlayPredicate extends AbstractStairsBlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        return switch (state.getValue(BlockStateProperties.STAIRS_SHAPE))
        {
            case STRAIGHT -> supportsEdgeStraight(state, side, edge, nullCullFace);
            case INNER_LEFT -> supportsEdgeInnerLeft(state, side, edge, nullCullFace);
            case INNER_RIGHT -> supportsEdgeInnerRight(state, side, edge, nullCullFace);
            case OUTER_LEFT -> supportsEdgeOuterLeft(state, side, edge, nullCullFace, unaligned);
            case OUTER_RIGHT -> supportsEdgeOuterRight(state, side, edge, nullCullFace, unaligned);
        };
    }

    @Override
    protected boolean isTopHalf(BlockState state)
    {
        return state.getValue(BlockStateProperties.HALF) == Half.TOP;
    }
}
