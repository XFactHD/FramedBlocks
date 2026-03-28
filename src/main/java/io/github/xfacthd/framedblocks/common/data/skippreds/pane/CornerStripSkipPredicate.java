package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedBoardBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@CullTest(BlockType.FRAMED_CORNER_STRIP)
public final class CornerStripSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);

            return switch (blockType) {
                case FRAMED_CORNER_STRIP -> testAgainstCornerStrip(
                        dir, type, adjState, side
                );
                case FRAMED_BOARD -> testAgainstWallBoard(
                        dir, type, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_STRIP)
    private static boolean testAgainstCornerStrip(Direction dir, SlopeType type, BlockState adjState, Direction side) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return getHalfDir(dir, type, side).isEqualTo(getHalfDir(adjDir, adjType, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_BOARD)
    private static boolean testAgainstWallBoard(Direction dir, SlopeType type, BlockState adjState, Direction side) {
        boolean faceAbsent = !FramedBoardBlock.isFacePresent(adjState, side.getOpposite());
        int edgeMask = faceAbsent ? FramedBoardBlock.computeEdgeMask(adjState, side.getOpposite()) : 0;
        return faceAbsent && getHalfDir(dir, type, side).isEqualTo(BoardSkipPredicate.getHalfDir(edgeMask, side.getOpposite()));
    }

    public static HalfDir getHalfDir(Direction dir, SlopeType type, Direction side) {
        Direction dirTwo = switch (type) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
        };
        if (side == dir) {
            return HalfDir.fromDirections(side, dirTwo);
        }
        if (side == dirTwo) {
            return HalfDir.fromDirections(side, dir);
        }
        return HalfDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, SlopeType type, Direction side) {
        Direction dirTwo = switch (type) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
        };
        if (side.getAxis() != dir.getAxis() && side.getAxis() != dirTwo.getAxis()) {
            return CornerDir.fromDirections(side, dir, dirTwo);
        }
        return CornerDir.NULL;
    }
}
