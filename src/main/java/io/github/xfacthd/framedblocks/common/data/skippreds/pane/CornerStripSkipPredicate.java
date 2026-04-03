package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
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
                case FRAMED_BOARD -> testAgainstBoard(
                        dir, type, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_STRIP)
    private static boolean testAgainstCornerStrip(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return PaneDirs.CornerStrip.getEdgeDir(dir, type, side).isEqualTo(PaneDirs.CornerStrip.getEdgeDir(adjDir, adjType, side.getOpposite())) ||
               PaneDirs.CornerStrip.getCornerDir(dir, type, side).isEqualTo(PaneDirs.CornerStrip.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_BOARD)
    private static boolean testAgainstBoard(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    ) {
        int adjFaces = adjState.getValue(PropertyHolder.FACES);
        return PaneDirs.CornerStrip.getEdgeDir(dir, type, side).isEqualTo(PaneDirs.Board.getSingleEdgeDir(adjFaces, side.getOpposite()));
    }
}
