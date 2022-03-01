package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class ElevatedSlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        if (adjState.is(FBContent.blockFramedElevatedSlopeSlab.get()))
        {
            return testAgainstElevatedSlopeSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedSlopeSlab.get()) || adjState.is(FBContent.blockFramedDoubleSlopeSlab.get()))
        {
            return testAgainstSlopeSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
        {
            return testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedSlab.get()))
        {
            return testAgainstSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedDoubleSlab.get()))
        {
            return testAgainstDoubleSlab(level, pos, dir, top, side);
        }
        else if (adjState.is(FBContent.blockFramedSlabEdge.get()))
        {
            return testAgainstSlabEdge(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedStairs.get()))
        {
            return testAgainstStairs(level, pos, dir, top, adjState, side);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(IBlockReader level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop != top) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
        }
        if (side == dir.getOpposite())
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(IBlockReader level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return side == dir.getOpposite() && adjDir == dir && adjTopHalf == top && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(IBlockReader level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (side != dir.getOpposite()) { return false; }

        return ((adjDir == dir && top) || (adjDir == dir.getOpposite() && !top)) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstSlab(IBlockReader level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        return adjTop == top && side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlab(IBlockReader level, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        return side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstSlabEdge(IBlockReader level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        return adjDir == dir && adjTop == top && side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstStairs(IBlockReader level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (adjTop != top || side != dir.getOpposite()) { return false; }

        return StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
    }


}
