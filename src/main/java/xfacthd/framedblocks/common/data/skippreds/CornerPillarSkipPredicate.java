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

public class CornerPillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);

        if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(world, pos, dir, adjState, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs.get() && side.getAxis() == Direction.Axis.Y)
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
        if (side != dir && side != dir.getCounterClockWise()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && dir == adjDir))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPillar(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstCorner(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((adjTop && side == Direction.DOWN) || (!adjTop && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (side == dir && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir.getCounterClockWise());
        }

        if (side == dir.getCounterClockWise() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }

        return false;
    }

    private boolean testAgainstStairs(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if ((adjTop && side == Direction.UP) || (!adjTop && side == Direction.DOWN))
        {
            if (adjShape == StairsShape.OUTER_LEFT)
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            if (adjShape == StairsShape.OUTER_RIGHT)
            {
                return dir.getCounterClockWise() == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(BlockGetter world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL)
        {
            if ((side == dir.getCounterClockWise() || side == dir) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else if (side.getAxis() == Direction.Axis.Y)
        {
            if ((side == Direction.DOWN) == adjType.isTop() && adjDir == dir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }
}