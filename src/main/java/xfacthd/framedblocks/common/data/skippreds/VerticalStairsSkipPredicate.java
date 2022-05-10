package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;

public class VerticalStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

            return switch (blockType)
            {
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, type, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, type, adjState, side);
                case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, type, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstCorner(level, pos, dir, type, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, dir, type, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(level, pos, dir, type, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, type, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((!type.isBottom() && !adjType.isTop() && side == Direction.DOWN) || (!type.isTop() && !adjType.isBottom() && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side);
        }

        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjBottom = adjState.getValue(BlockStateProperties.HALF) == Half.BOTTOM;

        if (type == StairsType.VERTICAL && ((side == Direction.UP && !adjBottom) || (side == Direction.DOWN && adjBottom)))
        {
            StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
            if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.getCounterClockWise() && adjShape == StairsShape.INNER_RIGHT))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else
        {
            if (type.isTop() == adjBottom && ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (side == dir.getClockWise() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }
        if (side == dir.getOpposite() && (adjDir == dir.getCounterClockWise() || adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir.getCounterClockWise());
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((Utils.isY(side) || side == dir.getOpposite() || side == dir.getClockWise()) && type.isTop() != adjTop && dir == adjDir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (type == StairsType.VERTICAL)
        {
            if ((side == dir.getClockWise() || side == dir.getOpposite()) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else if (Utils.isY(side))
        {
            if ((side == Direction.UP) == type.isTop() && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL || Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return adjTop != type.isTop() && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjTop == type.isTop()) { return false; }

        if ((adjRight && adjDir == dir && side == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getCounterClockWise() && side == dir))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) || (side == Direction.UP && type == StairsType.TOP_CORNER) || (side == Direction.DOWN && type == StairsType.BOTTOM_CORNER))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir != dir || adjTop != (side == Direction.DOWN)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side);
    }
}