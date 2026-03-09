package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalSlicedStairsBlockOverlayPredicate extends AbstractVerticalStairsBlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        if (!secondPart) return true;

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseDir = state.getValue(PropertyHolder.RIGHT) ? facing.getClockWise() : facing.getCounterClockWise();
        return side != baseDir;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction forward = right ? facing.getCounterClockWise() : facing;
        Direction toSecond = right ? facing.getOpposite() : facing.getClockWise();

        if (secondPart)
        {
            return side != toSecond.getOpposite() && edge != toSecond.getOpposite();
        }

        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        if (type == StairsType.VERTICAL)
        {
            return side != toSecond || edge != forward;
        }

        Direction vert = type.isTop() ? Direction.UP : Direction.DOWN;
        if (type != StairsType.TOP_BOTH && type != StairsType.BOTTOM_BOTH)
        {
            if (right == type.isCounterClockwise())
            {
                return true;
            }
            if (side == toSecond)
            {
                return edge != forward && edge != vert;
            }
        }
        if (side == vert)
        {
            return !nullCullFace || edge != forward;
        }
        if (side == forward.getOpposite())
        {
            return !nullCullFace || edge != vert.getOpposite();
        }
        return true;
    }
}
