package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalSlicedSlopedStairsPanelBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        if (secondPart)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return side != facing;
        }
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (secondPart)
        {
            return side != facing && edge != facing;
        }

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        if (side == facing.getOpposite())
        {
            return edge != dirTwo.getOpposite() && edge != dirThree.getOpposite();
        }
        if (side == dirTwo.getOpposite() || side == dirThree.getOpposite())
        {
            return edge != facing.getOpposite();
        }
        return false;
    }
}
