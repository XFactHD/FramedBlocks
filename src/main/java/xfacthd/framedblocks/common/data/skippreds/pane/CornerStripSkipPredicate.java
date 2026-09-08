package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_CORNER_STRIP)
public final class CornerStripSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);

            return switch (blockType)
            {
                case FRAMED_CORNER_STRIP -> testAgainstCornerStrip(
                        dir, type, adjState, side
                );
                case FRAMED_FLOOR_BOARD -> testAgainstFloorBoard(
                        dir, type, adjState, side
                );
                case FRAMED_WALL_BOARD -> testAgainstWallBoard(
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
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return PaneDirs.CornerStrip.getHalfDir(dir, type, side).isEqualTo(PaneDirs.CornerStrip.getHalfDir(adjDir, adjType, side.getOpposite())) ||
               PaneDirs.CornerStrip.getCornerDir(dir, type, side).isEqualTo(PaneDirs.CornerStrip.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLOOR_BOARD)
    private static boolean testAgainstFloorBoard(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return PaneDirs.CornerStrip.getHalfDir(dir, type, side).isEqualTo(PaneDirs.FloorBoard.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL_BOARD)
    private static boolean testAgainstWallBoard(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return PaneDirs.CornerStrip.getHalfDir(dir, type, side).isEqualTo(PaneDirs.WallBoard.getHalfDir(adjDir, side.getOpposite()));
    }
}
