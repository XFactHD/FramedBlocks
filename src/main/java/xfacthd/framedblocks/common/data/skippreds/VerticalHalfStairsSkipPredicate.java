package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class VerticalHalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        switch (adjBlock)
        {
            case FRAMED_VERTICAL_HALF_STAIRS: return testAgainstVerticalHalfStairs(world, pos, dir, top, adjState, side);
            case FRAMED_VERTICAL_STAIRS: return testAgainstVerticalStairs(world, pos, dir, top, adjState, side);
            case FRAMED_SLAB: return testAgainstSlab(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_SLAB: return testAgainstDoubleSlab(world, pos, dir, top, side);
            case FRAMED_SLAB_EDGE: return testAgainstSlabEdge(world, pos, dir, top, adjState, side);
            case FRAMED_SLAB_CORNER: return testAgainstSlabCorner(world, pos, dir, top, adjState, side);
            case FRAMED_STAIRS: return testAgainstStairs(world, pos, dir, top, adjState, side);
            case FRAMED_HALF_STAIRS: return testAgainstHalfStairs(world, pos, dir, top, adjState, side);
            case FRAMED_SLOPE_SLAB: return testAgainstSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_ELEVATED_SLOPE_SLAB: return testAgainstElevatedSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_SLOPE_SLAB: return testAgainstDoubleSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_INV_DOUBLE_SLOPE_SLAB: return testAgainstInverseDoubleSlopeSlab(world, pos, dir, top, adjState, side);
            default: return false;
        }
    }

    private static boolean testAgainstVerticalHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            if (adjTop == top || adjDir != dir)
            {
                return false;
            }
            return SideSkipPredicate.compareState(world, pos, side);
        }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(world, pos, side);
        }
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == Direction.DOWN && !top && !adjType.isTop()) || (side == Direction.UP && top && !adjType.isBottom()))
        {
            return adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        if (side == dir || side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop != top) { return false; }

        if ((side == dir || side == dir.getCounterClockWise()) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == top && adjDir == dir && (side == dir.getOpposite() || side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjTop = adjState.getValue(StairsBlock.HALF) == Half.TOP;
        StairsShape adjShape = adjState.getValue(StairsBlock.SHAPE);

        if (adjTop != top || !StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return false;
        }

        if (side == dir || side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjTop != top || adjDir != side) { return false; }

        if ((adjRight && adjDir == dir.getOpposite()) || (!adjRight && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()) && adjDir == side)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()) && (adjDir == side || adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (side != dir && side != dir.getCounterClockWise())
        {
            return false;
        }

        if ((!top && adjDir == side) || (top && adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}
