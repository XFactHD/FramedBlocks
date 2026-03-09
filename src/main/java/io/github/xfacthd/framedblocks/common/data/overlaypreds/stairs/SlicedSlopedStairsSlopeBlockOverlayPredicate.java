package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class SlicedSlopedStairsSlopeBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        if (secondPart)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return side != facing && side != facing.getCounterClockWise();
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
            return !DirUtils.isY(side) || (edge != facing && edge != facing.getCounterClockWise());
        }

        if (side == baseDir)
        {
            return edge != facing.getOpposite() && edge != facing.getClockWise();
        }
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return edge != baseDir;
        }
        return true;
    }
}
