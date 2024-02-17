package xfacthd.framedblocks.common.data.skippreds.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.DiagCornerDir;

@CullTest(BlockType.FRAMED_CHECKERED_CUBE_SEGMENT)
public final class CheckeredCubeSegmentSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            boolean second = state.getValue(PropertyHolder.SECOND);
            return switch (type)
            {
                case FRAMED_CHECKERED_CUBE_SEGMENT -> testAgainstCheckeredCubeSegment(
                        second, adjState, side
                );
                case FRAMED_CHECKERED_CUBE -> testAgainstCheckeredCube(
                        second, adjState, side
                );
                case FRAMED_CHECKERED_SLAB_SEGMENT -> testAgainstCheckeredSlabSegment(
                        second, adjState, side
                );
                case FRAMED_CHECKERED_SLAB -> testAgainstCheckeredSlab(
                        second, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        second, adjState, side
                );
                case FRAMED_CHECKERED_PANEL -> testAgainstCheckeredPanel(
                        second, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_CUBE_SEGMENT)
    private static boolean testAgainstCheckeredCubeSegment(
            boolean second, BlockState adjState, Direction side
    )
    {
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getDiagCornerDir(second, side).isEqualTo(getDiagCornerDir(adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_CUBE,
            partTargets = BlockType.FRAMED_CHECKERED_CUBE_SEGMENT
    )
    private static boolean testAgainstCheckeredCube(
            boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredCubeSegment(second, states.getA(), side) ||
               testAgainstCheckeredCubeSegment(second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT)
    private static boolean testAgainstCheckeredSlabSegment(
            boolean second, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getDiagCornerDir(second, side).isEqualTo(CheckeredSlabSegmentSkipPredicate.getDiagCornerDir(adjTop, adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_SLAB,
            partTargets = BlockType.FRAMED_CHECKERED_SLAB_SEGMENT
    )
    private static boolean testAgainstCheckeredSlab(
            boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredSlabSegment(second, states.getA(), side) ||
               testAgainstCheckeredSlabSegment(second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getDiagCornerDir(second, side).isEqualTo(CheckeredPanelSegmentSkipPredicate.getDiagCornerDir(adjDir, adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_PANEL,
            partTargets = BlockType.FRAMED_CHECKERED_PANEL_SEGMENT
    )
    private static boolean testAgainstCheckeredPanel(
            boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredPanelSegment(second, states.getA(), side) ||
               testAgainstCheckeredPanelSegment(second, states.getB(), side);
    }



    public static DiagCornerDir getDiagCornerDir(boolean second, Direction side)
    {
        return switch (side)
        {
            case DOWN -> second ? DiagCornerDir.DOWN_NE_SW : DiagCornerDir.DOWN_NW_SE;
            case UP -> second ? DiagCornerDir.UP_NW_SE : DiagCornerDir.UP_NE_SW;
            case NORTH -> second ? DiagCornerDir.NORTH_UW_DE : DiagCornerDir.NORTH_UE_DW;
            case SOUTH -> second ? DiagCornerDir.SOUTH_UE_DW : DiagCornerDir.SOUTH_UW_DE;
            case WEST -> second ? DiagCornerDir.WEST_UN_DS : DiagCornerDir.WEST_US_DN;
            case EAST -> second ? DiagCornerDir.EAST_US_DN : DiagCornerDir.EAST_UN_DS;
        };
    }
}
