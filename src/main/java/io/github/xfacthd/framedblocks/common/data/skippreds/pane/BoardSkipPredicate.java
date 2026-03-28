package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.pane.FramedBoardBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@CullTest(BlockType.FRAMED_BOARD)
public final class BoardSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (!FramedBoardBlock.isFacePresent(state, side)) {
            int edgeMask = FramedBoardBlock.computeEdgeMask(state, side);
            if (edgeMask == 0) {
                return false;
            }

            if (adjState.getBlock() == state.getBlock()) {
                return testAgainstBoard(edgeMask, adjState, side);
            }
            if (adjState.getBlock() == FBContent.BLOCK_FRAMED_CORNER_STRIP.value()) {
                return testAgainstCornerStrip(edgeMask, adjState, side);
            }
        }

        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_BOARD)
    private static boolean testAgainstBoard(int edgeMask, BlockState adjState, Direction side) {
        boolean faceAbsent = !FramedBoardBlock.isFacePresent(adjState, side.getOpposite());
        return faceAbsent && edgeMask == FramedBoardBlock.computeEdgeMask(adjState, side.getOpposite());
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_STRIP)
    private static boolean testAgainstCornerStrip(int edgeMask, BlockState adjState, Direction side) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return getHalfDir(edgeMask, side).isEqualTo(CornerStripSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    public static HalfDir getHalfDir(int edgeMask, Direction side) {
        if (Integer.bitCount(edgeMask) == 1) {
            int face = Integer.numberOfTrailingZeros(edgeMask);
            return HalfDir.fromDirections(side, Direction.from3DDataValue(face));
        }
        return HalfDir.NULL;
    }
}
