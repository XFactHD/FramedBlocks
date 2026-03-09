package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class SlicedSlopedStairsSlabBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        if (secondPart)
        {
            Direction baseDir = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
            return side != baseDir;
        }
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseDir = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (secondPart)
        {
            return side != baseDir && edge != baseDir;
        }

        if (side == baseDir.getOpposite())
        {
            return edge != facing && edge != facing.getCounterClockWise();
        }
        if (side == facing || side == facing.getCounterClockWise())
        {
            return edge != baseDir.getOpposite();
        }
        return true;
    }
}
