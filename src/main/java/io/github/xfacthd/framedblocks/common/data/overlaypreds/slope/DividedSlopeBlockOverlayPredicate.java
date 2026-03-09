package io.github.xfacthd.framedblocks.common.data.overlaypreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DividedSlopeBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            return switch (side)
            {
                case UP -> secondPart;
                case DOWN -> !secondPart;
                default -> true;
            };
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getClockWise()) return secondPart;
        if (side == facing.getCounterClockWise()) return !secondPart;
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        return true;
    }
}
