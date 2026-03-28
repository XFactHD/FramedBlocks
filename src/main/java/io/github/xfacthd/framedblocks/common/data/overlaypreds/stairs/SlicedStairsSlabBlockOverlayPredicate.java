package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

public final class SlicedStairsSlabBlockOverlayPredicate extends AbstractVerticalStairsBlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        if (secondPart) {
            boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
            Direction baseDir = top ? Direction.UP : Direction.DOWN;
            return side != baseDir;
        }
        return true;
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction baseDir = top ? Direction.UP : Direction.DOWN;
        return switch (state.getValue(BlockStateProperties.STAIRS_SHAPE)) {
            case STRAIGHT -> {
                if (secondPart) {
                    yield side != baseDir && edge != baseDir;
                }
                if (side == baseDir.getOpposite()) {
                    yield edge != facing;
                }
                yield side == facing.getOpposite() || edge != baseDir.getOpposite();
            }
            case INNER_LEFT, INNER_RIGHT -> {
                if (secondPart) {
                    yield side != baseDir && edge != baseDir && supportsEdgeVertical(state, side, edge, nullCullFace);
                }
                yield edge != baseDir.getOpposite();
            }
            case OUTER_LEFT -> {
                if (secondPart) {
                    yield side != baseDir && edge != baseDir;
                }
                yield edge != baseDir.getOpposite() || (side != facing && side != facing.getCounterClockWise());
            }
            case OUTER_RIGHT -> {
                if (secondPart) {
                    yield side != baseDir && edge != baseDir;
                }
                yield edge != baseDir.getOpposite() || (side != facing && side != facing.getClockWise());
            }
        };
    }
}
