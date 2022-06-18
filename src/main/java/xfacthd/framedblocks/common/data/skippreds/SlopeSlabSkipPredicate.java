package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

            return switch (type)
            {
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(level, pos, dir, topHalf, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_SLAB -> testAgainstSlab(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(level, pos, dir, topHalf, side);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, dir, topHalf, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, dir, topHalf, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, topHalf, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return adjDir == dir && adjTop == top && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        if (side == dir)
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return side == dir && adjDir == dir && adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        if (side == dir)
        {
            return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir, dir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return ((adjDir == dir && topHalf && !top) || (adjDir == dir.getOpposite() && !topHalf && top)) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        if (side == dir && ((adjDir == dir && !topHalf) || (adjDir == dir.getOpposite() && topHalf)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return side == dir && adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, Direction side)
    {
        return side == dir && SideSkipPredicate.compareState(level, pos, side, dir, topHalf ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return side == dir && adjDir == dir.getOpposite() && adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (topHalf == adjTop && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return side == dir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir == dir && side == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, topHalf ? Direction.UP : Direction.DOWN);
        }
        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != topHalf || side != dir) { return false; }

        if (adjDir == dir.getOpposite() || adjDir == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }
}
