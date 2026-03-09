package io.github.xfacthd.framedblocks.common.data.overlaypreds.slope;

import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class StackedPyramidSlabBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return (!secondPart && side != facing) || (secondPart && side != facing.getOpposite());
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if ((!secondPart && side != facing) || (secondPart && side != facing.getOpposite()))
        {
            return !secondPart || edge != facing;
        }
        return false;
    }
}
