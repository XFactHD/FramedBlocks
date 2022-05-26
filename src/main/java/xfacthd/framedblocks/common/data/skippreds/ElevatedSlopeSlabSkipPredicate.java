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

public class ElevatedSlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        if (side == dir || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return SideSkipPredicate.CTM.test(world, pos, state, adjState, side);
        }

        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }
        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();

        switch (adjBlock)
        {
            case FRAMED_ELEVATED_SLOPE_SLAB: return testAgainstElevatedSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_SLOPE_SLAB:
            case FRAMED_DOUBLE_SLOPE_SLAB: return testAgainstSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_INV_DOUBLE_SLOPE_SLAB: return testAgainstInverseDoubleSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_SLAB: return testAgainstSlab(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_SLAB: return testAgainstDoubleSlab(world, pos, dir, top, side);
            case FRAMED_SLAB_EDGE: return testAgainstSlabEdge(world, pos, dir, top, adjState, side);
            case FRAMED_STAIRS: return testAgainstStairs(world, pos, dir, top, adjState, side);
            case FRAMED_VERTICAL_HALF_STAIRS: return testAgainstVerticalHalfStairs(world, pos, dir, top, adjState, side);
            default: return false;
        }
    }

    private static boolean testAgainstElevatedSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop != top) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
        }
        if (side == dir.getOpposite())
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return side == dir.getOpposite() && adjDir == dir && adjTopHalf == top && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (side != dir.getOpposite()) { return false; }

        return ((adjDir == dir && top) || (adjDir == dir.getOpposite() && !top)) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        return adjTop == top && side == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        return side == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        return adjDir == dir && adjTop == top && side == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (adjTop != top || side != dir.getOpposite()) { return false; }

        return StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstVerticalHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (side != dir.getOpposite() || adjTop != top) { return false; }

        if (adjDir == dir || adjDir == dir.getClockWise())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}
