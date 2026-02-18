package io.github.xfacthd.framedblocks.api.predicate.overlay;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

final class OnlyFirstPartBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return !secondPart;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        return !secondPart;
    }
}
