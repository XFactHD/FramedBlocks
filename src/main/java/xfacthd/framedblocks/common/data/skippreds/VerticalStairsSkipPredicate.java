package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class VerticalStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(level, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(level, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(level, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(level, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(level, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(level, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get() && type != StairsType.VERTICAL && side.getAxis() != Direction.Axis.Y)
        {
            return testAgainstEdge(level, pos, dir, type, adjState, side);
        }

        return false;
    }

    private boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((!type.isBottom() && !adjType.isTop() && side == Direction.DOWN) || (!type.isTop() && !adjType.isBottom() && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side);
        }

        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjBottom = adjState.getValue(BlockStateProperties.HALF) == Half.BOTTOM;

        if (type == StairsType.VERTICAL && ((side == Direction.UP && !adjBottom) || (side == Direction.DOWN && adjBottom)))
        {
            StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
            if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.getCounterClockWise() && adjShape == StairsShape.INNER_RIGHT))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else
        {
            if (type.isTop() == adjBottom && ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (side == dir.getClockWise() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }
        if (side == dir.getOpposite() && (adjDir == dir.getCounterClockWise() || adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir.getCounterClockWise());
        }
        return false;
    }

    private boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side.getAxis() == Direction.Axis.Y || side == dir.getOpposite() || side == dir.getClockWise()) && type.isTop() != adjTop && dir == adjDir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (type == StairsType.VERTICAL)
        {
            if ((side == dir.getClockWise() || side == dir.getOpposite()) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else if (side.getAxis() == Direction.Axis.Y)
        {
            if ((side == Direction.UP) == type.isTop() && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return adjTop != type.isTop() && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }
}