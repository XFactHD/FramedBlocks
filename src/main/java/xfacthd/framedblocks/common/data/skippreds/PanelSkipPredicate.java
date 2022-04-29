package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;

public class PanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        if (side == dir) { return SideSkipPredicate.CTM.test(level, pos, state, adjState, side); }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
                    {
                        case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, adjState, side);
                        case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, adjState, side);
                        case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, dir, adjState, side);
                        case FRAMED_SLAB_EDGE -> testAgainstEdge(level, pos, dir, adjState, side);
                        case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, adjState, side);
                        case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, adjState, side);
                        case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, adjState, side);
                        default -> false;
                    };
        }

        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        return dir == adjState.getValue(PropertyHolder.FACING_HOR) && SideSkipPredicate.compareState(level, pos, side, dir);
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        return (dir == adjDir || dir == adjDir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir);
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (adjDir != dir) { return false; }

        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop))
        {
            return adjShape == StairsShape.STRAIGHT && SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjType == StairsType.VERTICAL && SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side.getAxis() == dir.getAxis()) { return false; }

        if ((adjRight && adjDir == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getClockWise()))
        {
            if (Utils.isY(side))
            {
                return (side == Direction.DOWN) == adjTop && SideSkipPredicate.compareState(level, pos, side, dir);
            }
            else
            {
                return (side == adjDir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir);
            }
        }

        return false;
    }
}