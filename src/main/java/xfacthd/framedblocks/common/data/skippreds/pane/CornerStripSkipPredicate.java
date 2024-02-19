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
import xfacthd.framedblocks.common.data.skippreds.*;

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
    private static boolean testAgainstCornerStrip(Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return getHalfDir(dir, type, side).isEqualTo(getHalfDir(adjDir, adjType, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLOOR_BOARD)
    private static boolean testAgainstFloorBoard(Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        boolean top = adjState.getValue(FramedProperties.TOP);
        return getHalfDir(dir, type, side).isEqualTo(FloorBoardSkipPredicate.getHalfDir(top, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL_BOARD)
    private static boolean testAgainstWallBoard(Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, type, side).isEqualTo(WallBoardSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }



    public static HalfDir getHalfDir(Direction dir, SlopeType type, Direction side)
    {
        Direction dirTwo = switch (type)
        {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
        };
        if (side == dir)
        {
            return HalfDir.fromDirections(side, dirTwo);
        }
        else if (side == dirTwo)
        {
            return HalfDir.fromDirections(side, dir);
        }
        return HalfDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, SlopeType type, Direction side)
    {
        Direction dirTwo = switch (type)
        {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
        };
        if (side.getAxis() != dir.getAxis() && side.getAxis() != dirTwo.getAxis())
        {
            return CornerDir.fromDirections(side, dir, dirTwo);
        }
        return CornerDir.NULL;
    }
}
