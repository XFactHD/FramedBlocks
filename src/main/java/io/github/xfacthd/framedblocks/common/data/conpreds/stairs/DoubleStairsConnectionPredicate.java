package io.github.xfacthd.framedblocks.common.data.conpreds.stairs;

import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jspecify.annotations.Nullable;

public final class DoubleStairsConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        return switch (shape) {
            case STRAIGHT -> {
                if (side == facing || side == dirTwo) {
                    yield true;
                }
                if (side.getAxis() == facing.getClockWise().getAxis()) {
                    yield edge == facing || edge == dirTwo;
                }
                if (side == dirTwo.getOpposite()) {
                    yield edge != null && edge.getAxis() == facing.getAxis();
                }
                if (side == facing.getOpposite()) {
                    yield edge != null && DirUtils.isY(edge);
                }
                yield false;
            }
            case INNER_LEFT -> {
                if (side == facing || side == facing.getCounterClockWise() || side == dirTwo) {
                    yield true;
                }
                if (side == facing.getClockWise()) {
                    yield edge == facing || edge == dirTwo;
                }
                if (side == facing.getOpposite()) {
                    yield edge == facing.getCounterClockWise() || edge == dirTwo;
                }
                if (side == dirTwo.getOpposite()) {
                    yield edge == facing || edge == facing.getCounterClockWise();
                }
                yield false;
            }
            case INNER_RIGHT -> {
                if (side == facing || side == facing.getClockWise() || side == dirTwo) {
                    yield true;
                }
                if (side == facing.getCounterClockWise()) {
                    yield edge == facing || edge == dirTwo;
                }
                if (side == facing.getOpposite()) {
                    yield edge == facing.getClockWise() || edge == dirTwo;
                }
                if (side == dirTwo.getOpposite()) {
                    yield edge == facing || edge == facing.getClockWise();
                }
                yield false;
            }
            case OUTER_LEFT -> {
                if (side == dirTwo) {
                    yield true;
                }
                if (side == dirTwo.getOpposite()) {
                    yield edge == facing.getClockWise() || edge == facing.getOpposite();
                }
                if (side == facing) {
                    yield edge == facing.getCounterClockWise() || edge == dirTwo;
                }
                if (side == facing.getCounterClockWise()) {
                    yield edge == facing || edge == dirTwo;
                }
                if (side == facing.getClockWise() || side == facing.getOpposite()) {
                    yield edge != null && DirUtils.isY(edge);
                }
                yield false;
            }
            case OUTER_RIGHT -> {
                if (side == dirTwo) {
                    yield true;
                }
                if (side == dirTwo.getOpposite()) {
                    yield edge == facing.getCounterClockWise() || edge == facing.getOpposite();
                }
                if (side == facing) {
                    yield edge == facing.getClockWise() || edge == dirTwo;
                }
                if (side == facing.getClockWise()) {
                    yield edge == facing || edge == dirTwo;
                }
                if (side == facing.getCounterClockWise() || side == facing.getOpposite()) {
                    yield edge != null && DirUtils.isY(edge);
                }
                yield false;
            }
        };
    }
}
