package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        if (adjState.is(FBContent.blockFramedSlopeSlab.get()))
        {
            return testAgainstSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedElevatedSlopeSlab.get()))
        {
            return testAgainstElevatedSlopeSlab(level, pos, dir, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedDoubleSlopeSlab.get()))
        {
            return testAgainstDoubleSlopeSlab(level, pos, dir, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
        {
            return testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedSlab.get()))
        {
            return testAgainstSlab(level, pos, dir, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedDoubleSlab.get()))
        {
            return testAgainstDoubleSlab(level, pos, dir, topHalf, side);
        }
        else if (adjState.is(FBContent.blockFramedSlabEdge.get()))
        {
            return testAgainstSlabEdge(level, pos, dir, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedStairs.get()))
        {
            return testAgainstStairs(level, pos, dir, topHalf, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedVerticalHalfStairs.get()))
        {
            return testAgainstVerticalHalfStairs(level, pos, dir, topHalf, adjState, side);
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
            return adjDir == dir && adjTop == top && SideSkipPredicate.compareState(level, pos, side, dir);
        }
        if (side == dir)
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side, dir);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return side == dir && adjDir == dir && adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir);
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return (adjDir == dir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir);
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
            return ((adjDir == dir && topHalf && !top) || (adjDir == dir.getOpposite() && !topHalf && top)) && SideSkipPredicate.compareState(level, pos, side, dir);
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
        return side == dir && adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir);
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, Direction side)
    {
        return side == dir && SideSkipPredicate.compareState(level, pos, side, dir, topHalf ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return side == dir && adjDir == dir.getOpposite() && adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir);
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (topHalf == adjTop && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return side == dir && SideSkipPredicate.compareState(level, pos, side, dir);
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
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }

        return false;
    }
}
