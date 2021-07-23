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

public class PanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        if (side == dir) { return SideSkipPredicate.CTM.test(world, pos, state, adjState, side); }

        if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(world, pos, dir, adjState, side);
        }
        return false;
    }

    private boolean testAgainstPanel(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        return dir == adjState.getValue(PropertyHolder.FACING_HOR) && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private boolean testAgainstDoublePanel(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        return (dir == adjDir || dir == adjDir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private boolean testAgainstPillar(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstEdge(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (adjDir != dir) { return false; }

        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstStairs(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop))
        {
            return adjShape == StairsShape.STRAIGHT && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjType == StairsType.VERTICAL && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }
}