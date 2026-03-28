package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractVerticalStairsBlockOverlayPredicate implements BlockOverlayPredicate {
    protected static boolean supportsEdgeVertical(BlockState state, Direction side, Direction edge, boolean nullCullFace) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        return supportsEdgeVertical(facing, side, edge, nullCullFace);
    }

    protected static boolean supportsEdgeVertical(Direction facing, Direction side, Direction edge, boolean nullCullFace) {
        if (side == facing.getOpposite()) {
            return !nullCullFace || edge != facing.getCounterClockWise();
        }
        if (side == facing.getClockWise()) {
            return !nullCullFace || edge != facing;
        }
        return true;
    }

    protected static boolean supportsEdgeTopForward(BlockState state, Direction side, Direction edge, boolean nullCullFace) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == Direction.UP) {
            return !nullCullFace || edge != facing.getCounterClockWise();
        }
        return true;
    }

    protected static boolean supportsEdgeTopCounterClockwise(BlockState state, Direction side, Direction edge, boolean nullCullFace) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getClockWise() || side == Direction.UP) {
            return !nullCullFace || edge != facing;
        }
        return true;
    }

    protected static boolean supportsEdgeTopBoth(BlockState state, Direction side, Direction edge, boolean nullCullFace, boolean unaligned) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite()) {
            return !nullCullFace || !unaligned || edge != facing.getCounterClockWise();
        }
        if (side == facing.getClockWise()) {
            return !nullCullFace || !unaligned || edge != facing;
        }
        if (side == Direction.UP) {
            return !nullCullFace || !unaligned || (edge != facing && edge != facing.getCounterClockWise());
        }
        return true;
    }

    protected static boolean supportsEdgeBottomForward(BlockState state, Direction side, Direction edge, boolean nullCullFace) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == Direction.DOWN) {
            return !nullCullFace || edge != facing.getCounterClockWise();
        }
        return true;
    }

    protected static boolean supportsEdgeBottomCounterClockwise(BlockState state, Direction side, Direction edge, boolean nullCullFace) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getClockWise() || side == Direction.DOWN) {
            return !nullCullFace || edge != facing;
        }
        return true;
    }

    protected static boolean supportsEdgeBottomBoth(BlockState state, Direction side, Direction edge, boolean nullCullFace, boolean unaligned) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite()) {
            return !nullCullFace || !unaligned || edge != facing.getCounterClockWise();
        }
        if (side == facing.getClockWise()) {
            return !nullCullFace || !unaligned || edge != facing;
        }
        if (side == Direction.DOWN) {
            return !nullCullFace || !unaligned || (edge != facing && edge != facing.getCounterClockWise());
        }
        return true;
    }
}
