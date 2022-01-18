package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public class SlabCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(level, pos, dir, top, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedHalfStairs.get()))
        {
            return testAgainstHalfStairs(level, pos, dir, top, adjState, side);
        }

        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return top == adjTop && SideSkipPredicate.compareState(level, pos, side);
        }
        if ((side == Direction.DOWN && !top && adjTop) || (side == Direction.UP && top && !adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir))
        {
            return top == adjTop && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (top != adjTop) { return false; }

        if (adjShape == StairsShape.OUTER_LEFT)
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side);
        }
        if (adjShape == StairsShape.OUTER_RIGHT)
        {
            return dir.getCounterClockWise() == adjDir && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType.isTop() == top || dir != adjDir) { return false; }

        if (side.getAxis() == Direction.Axis.Y || side == dir || side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjTop != top) { return false; }
        if ((adjRight && adjDir != dir.getCounterClockWise()) || (!adjRight && adjDir != dir)) { return false; }

        if ((adjRight && side == dir.getCounterClockWise()) || (!adjRight && side == dir) || (side.getAxis() == Direction.Axis.Y && (side == Direction.UP) == adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }
}