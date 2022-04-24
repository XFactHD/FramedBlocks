package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (Utils.isY(side)) { return SideSkipPredicate.CTM.test(level, pos, state, adjState, side); }

        boolean top = state.getValue(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlab.get())
        {
            return testAgainstSlab(level, pos, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            return testAgainstDoubleSlab(level, pos, top, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(level, pos, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(level, pos, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedSlopeSlab.get()))
        {
            return testAgainstSlopeSlab(level, pos, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedElevatedSlopeSlab.get()))
        {
            return testAgainstElevatedSlopeSlab(level, pos, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedDoubleSlopeSlab.get()))
        {
            return testAgainstDoubleSlopeSlab(level, pos, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
        {
            return testAgainstInverseDoubleSlopeSlab(level, pos, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedVerticalHalfStairs.get()))
        {
            return testAgainstVerticalHalfStairs(level, pos, top, adjState, side);
        }

        return false;
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        if (top != adjState.getValue(PropertyHolder.TOP)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, boolean top, Direction side)
    {
        Direction face = top ? Direction.UP : Direction.DOWN;
        return SideSkipPredicate.compareState(level, pos, side, face);
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        if (top != adjState.getValue(PropertyHolder.TOP)) { return false; }
        if (adjState.getValue(PropertyHolder.FACING_HOR) != side.getOpposite()) { return false; }

        return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (top == adjTop && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (Utils.isY(side)) { return false; }

        return adjDir == side.getOpposite() && adjTopHalf == top && SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (Utils.isY(side)) { return false; }

        return adjDir == side && adjTop == top && SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (Utils.isY(side)) { return false; }

        return (adjDir == side || adjDir == side.getOpposite()) && adjTopHalf == top && SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (Utils.isY(side)) { return false; }

        return ((adjDir == side && !top) || (adjDir == side.getOpposite() && top)) && SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (Utils.isY(side)) { return false; }

        if (adjTop == top && (adjDir == side.getOpposite() || adjDir == side.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
        }

        return false;
    }
}