package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_BOARD)
public final class BoardSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        int faces = state.getValue(PropertyHolder.FACES);
        if (PaneDirs.Board.testEarlyExit(faces, side)) {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_BOARD -> testAgainstBoard(
                        faces, adjState, side
                );
                case FRAMED_HALF_BOARD -> testAgainstHalfBoard(
                        faces, adjState, side
                );
                case FRAMED_INNER_CORNER_BOARD -> testAgainstInnerCornerBoard(
                        faces, adjState, side
                );
                case FRAMED_CORNER_STRIP -> testAgainstCornerStrip(
                        faces, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_BOARD)
    private static boolean testAgainstBoard(
            int faces, BlockState adjState, Direction side
    ) {
        int adjFaces = adjState.getValue(PropertyHolder.FACES);
        return PaneDirs.Board.getEdgeMaskDir(faces, side) == PaneDirs.Board.getEdgeMaskDir(adjFaces, side.getOpposite());
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_BOARD)
    private static boolean testAgainstHalfBoard(
            int faces, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.Board.getSingleEdgeDir(faces, side).isEqualTo(PaneDirs.HalfBoard.getEdgeDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_BOARD)
    private static boolean testAgainstInnerCornerBoard(
            int faces, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.Board.getSingleEdgeDir(faces, side).isEqualTo(PaneDirs.InnerCornerBoard.getEdgeDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_STRIP)
    private static boolean testAgainstCornerStrip(
            int faces, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return PaneDirs.Board.getSingleEdgeDir(faces, side).isEqualTo(PaneDirs.CornerStrip.getEdgeDir(adjDir, adjType, side.getOpposite()));
    }
}
