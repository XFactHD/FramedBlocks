package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;

public class VerticalHalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if (adjState.is(FBContent.blockFramedVerticalHalfStairs.get()))
        {
            return testAgainstVerticalHalfStairs(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedVerticalStairs.get()))
        {
            return testAgainstVerticalStairs(level, pos, dir, top, adjState, side);
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
        else if (adjState.is(FBContent.blockFramedSlabCorner.get()))
        {
            return testAgainstSlabCorner(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedStairs.get()))
        {
            return testAgainstStairs(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedHalfStairs.get()))
        {
            return testAgainstHalfStairs(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedSlopeSlab.get()))
        {
            return testAgainstSlopeSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedElevatedSlopeSlab.get()))
        {
            return testAgainstElevatedSlopeSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedDoubleSlopeSlab.get()))
        {
            return testAgainstDoubleSlopeSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
        {
            return testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, adjState, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            if (adjTop == top || adjDir != dir)
            {
                return false;
            }
            return SideSkipPredicate.compareState(level, pos, side);
        }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == Direction.DOWN && !top && !adjType.isTop()) || (side == Direction.UP && top && !adjType.isBottom()))
        {
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        if (side == dir || side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top) { return false; }

        if ((side == dir || side == dir.getCounterClockWise()) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && adjDir == dir && (side == dir.getOpposite() || side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);

        if (adjTop != top || !StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return false;
        }

        if (side == dir || side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && (side == dir.getOpposite() || side == dir.getClockWise()) && adjDir == side)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()) && adjDir == side)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && (side == dir || side == dir.getCounterClockWise()) && (adjDir == side || adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side != dir && side != dir.getCounterClockWise())
        {
            return false;
        }

        if ((!top && adjDir == side) || (top && adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }
}
