package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class SlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }

        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }
        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        switch (adjBlock)
        {
            case FRAMED_SLOPE_SLAB: return testAgainstSlopeSlab(world, pos, dir, top, topHalf, adjState, side);
            case FRAMED_ELEVATED_SLOPE_SLAB: return testAgainstElevatedSlopeSlab(world, pos, dir, topHalf, adjState, side);
            case FRAMED_DOUBLE_SLOPE_SLAB: return testAgainstDoubleSlopeSlab(world, pos, dir, topHalf, adjState, side);
            case FRAMED_INV_DOUBLE_SLOPE_SLAB: return testAgainstInverseDoubleSlopeSlab(world, pos, dir, top, topHalf, adjState, side);
            case FRAMED_SLAB: return testAgainstSlab(world, pos, dir, topHalf, adjState, side);
            case FRAMED_DOUBLE_SLAB: return testAgainstDoubleSlab(world, pos, dir, topHalf, side);
            case FRAMED_SLAB_EDGE: return testAgainstSlabEdge(world, pos, dir, topHalf, adjState, side);
            case FRAMED_STAIRS: return testAgainstStairs(world, pos, dir, topHalf, adjState, side);
            case FRAMED_VERTICAL_HALF_STAIRS: return testAgainstVerticalHalfStairs(world, pos, dir, topHalf, adjState, side);
            default: return false;
        }
    }

    private static boolean testAgainstSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return adjDir == dir && adjTop == top && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        if (side == dir)
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side, dir);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        return side == dir && adjDir == dir && adjTop == topHalf && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private static boolean testAgainstDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        if (side == dir)
        {
            return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir, dir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return ((adjDir == dir && topHalf && !top) || (adjDir == dir.getOpposite() && !topHalf && top)) && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        if (side == dir && ((adjDir == dir && !topHalf) || (adjDir == dir.getOpposite() && topHalf)))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir, dir.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlab(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        return side == dir && adjTop == topHalf && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private static boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, Direction side)
    {
        return side == dir && SideSkipPredicate.compareState(world, pos, side, dir, topHalf ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        return side == dir && adjDir == dir.getOpposite() && adjTop == topHalf && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (topHalf == adjTop && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return side == dir && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop != topHalf || side != dir) { return false; }

        if (adjDir == dir.getOpposite() || adjDir == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }

        return false;
    }
}
