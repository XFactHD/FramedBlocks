package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractStairsBlockOverlayPredicate implements BlockOverlayPredicate
{
    protected final boolean supportsEdgeStraight(BlockState state, Direction side, Direction edge, boolean nullCullFace)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseFace = isTopHalf(state) ? Direction.UP : Direction.DOWN;
        if (side == baseFace.getOpposite() && nullCullFace)
        {
            return edge != facing;
        }
        if (side == facing.getOpposite() && nullCullFace)
        {
            return edge != baseFace;
        }
        return true;
    }

    protected boolean supportsEdgeInnerLeft(BlockState state, Direction side, Direction edge, boolean nullCullFace)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseFace = isTopHalf(state) ? Direction.UP : Direction.DOWN;
        if (side == facing.getOpposite())
        {
            return !nullCullFace || (edge != facing.getCounterClockWise() && edge != baseFace);
        }
        if (side == facing.getClockWise())
        {
            return !nullCullFace || (edge != facing && edge != baseFace);
        }
        if (side == baseFace.getOpposite())
        {
            return !nullCullFace || (edge != facing && edge != facing.getCounterClockWise());
        }
        return true;
    }

    protected boolean supportsEdgeInnerRight(BlockState state, Direction side, Direction edge, boolean nullCullFace)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseFace = isTopHalf(state) ? Direction.UP : Direction.DOWN;
        if (side == facing.getOpposite())
        {
            return !nullCullFace || (edge != facing.getClockWise() && edge != baseFace);
        }
        if (side == facing.getCounterClockWise())
        {
            return !nullCullFace || (edge != facing && edge != baseFace);
        }
        if (side == baseFace.getOpposite())
        {
            return !nullCullFace || (edge != facing && edge != facing.getClockWise());
        }
        return true;
    }

    protected boolean supportsEdgeOuterLeft(BlockState state, Direction side, Direction edge, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseFace = isTopHalf(state) ? Direction.UP : Direction.DOWN;
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return !nullCullFace || edge != baseFace;
        }
        if (side == baseFace.getOpposite())
        {
            return !unaligned || (edge != facing && edge != facing.getCounterClockWise());
        }
        return true;
    }

    protected boolean supportsEdgeOuterRight(BlockState state, Direction side, Direction edge, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction baseFace = isTopHalf(state) ? Direction.UP : Direction.DOWN;
        if (side == facing.getOpposite() || side == facing.getCounterClockWise())
        {
            return !nullCullFace || edge != baseFace;
        }
        if (side == baseFace.getOpposite())
        {
            return !unaligned || (edge != facing && edge != facing.getClockWise());
        }
        return true;
    }

    protected abstract boolean isTopHalf(BlockState state);
}
