package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

public final class DividedStairsBlockOverlayPredicate extends AbstractStairsBlockOverlayPredicate {
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        return switch (state.getValue(BlockStateProperties.STAIRS_SHAPE)) {
            case STRAIGHT -> side.getAxis() != facing.getClockWise().getAxis();
            case INNER_LEFT, OUTER_LEFT -> !secondPart || side != facing.getCounterClockWise();
            case INNER_RIGHT, OUTER_RIGHT -> secondPart || side != facing.getClockWise();
        };
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = isTopHalf(state) ? Direction.UP : Direction.DOWN;
        return switch (state.getValue(BlockStateProperties.STAIRS_SHAPE)) {
            case STRAIGHT -> {
                if (nullCullFace && side.getAxis() == facing.getClockWise().getAxis()) {
                    yield false;
                }
                if (unaligned && edge.getAxis() == facing.getClockWise().getAxis()) {
                    yield false;
                }
                yield supportsEdgeStraight(state, side, edge, nullCullFace);
            }
            case INNER_LEFT -> {
                if (!secondPart) {
                    yield side != facing.getClockWise() || (edge != facing && edge != dirTwo);
                }
                if (side == facing.getCounterClockWise() || edge == facing.getCounterClockWise()) {
                    yield false;
                }
                yield supportsEdgeStraight(state, side, edge, nullCullFace);
            }
            case INNER_RIGHT -> {
                if (secondPart) {
                    yield side != facing.getCounterClockWise() || (edge != facing && edge != dirTwo);
                }
                if (side == facing.getClockWise() || edge == facing.getClockWise()) {
                    yield false;
                }
                yield supportsEdgeStraight(state, side, edge, nullCullFace);
            }
            case OUTER_LEFT -> {
                if (secondPart) {
                    yield side != facing.getCounterClockWise() || edge != facing.getCounterClockWise();
                }
                if ((!unaligned && edge == facing.getOpposite()) || edge == dirTwo || (unaligned && side == facing.getClockWise() && edge == dirTwo.getOpposite())) {
                    yield false;
                }
                yield supportsEdgeStraight(state, side, edge, nullCullFace);
            }
            case OUTER_RIGHT -> {
                if (!secondPart) {
                    yield side != facing.getClockWise() || edge != facing.getClockWise();
                }
                if ((!unaligned && edge == facing.getOpposite()) || edge == dirTwo || (unaligned && side == facing.getCounterClockWise() && edge == dirTwo.getOpposite())) {
                    yield false;
                }
                yield supportsEdgeStraight(state, side, edge, nullCullFace);
            }
        };
    }

    @Override
    protected boolean isTopHalf(BlockState state) {
        return state.getValue(BlockStateProperties.HALF) == Half.TOP;
    }
}
