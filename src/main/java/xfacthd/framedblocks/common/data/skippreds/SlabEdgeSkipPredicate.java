package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public class SlabEdgeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlab.get())
        {
            return testAgainstSlab(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            return testAgainstDoubleSlab(level, pos, dir, top, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(level, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(level, pos, dir, top, adjState, side);
        }
        return false;
    }

    private boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (side == dir && adjDir == side.getOpposite())
        {
            return top == adjTop && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return dir == adjDir && top == adjTop && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side.getAxis() == Direction.Axis.Y && dir == adjDir)
        {
            return top != adjTop && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private boolean testAgainstSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side != dir || top != adjState.getValue(PropertyHolder.TOP)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side);
    }

    private boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        if (side != dir) { return false; }

        Direction face = top ? Direction.UP : Direction.DOWN;
        return SideSkipPredicate.compareState(level, pos, side, face);
    }

    private boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return top == adjTop && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (dir != adjDir && dir != adjDir.getOpposite()) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            if (adjShape != StairsShape.STRAIGHT || dir != adjDir) { return false; }
            return top == adjTop && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (top == adjTop && side == dir && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL) { return false; }
        if (((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise())))
        {
            return top != adjType.isTop() && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }
}