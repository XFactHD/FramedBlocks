package io.github.xfacthd.framedblocks.common.data.overlaypreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DividedPanelVerticalBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return Utils.isY(side) || side.getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if ((!secondPart && side == facing.getClockWise()) || (secondPart && side == facing.getCounterClockWise()))
        {
            return false;
        }
        return (!secondPart && edge != facing.getClockWise()) || (secondPart && edge != facing.getCounterClockWise());
    }
}
