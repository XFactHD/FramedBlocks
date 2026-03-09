package io.github.xfacthd.framedblocks.common.data.overlaypreds.pillar;

import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class SplitPillarSocketBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return !secondPart || side != facing;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        if (secondPart)
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            return side != facing && (!nullCullFace || edge != facing);
        }
        return true;
    }
}
