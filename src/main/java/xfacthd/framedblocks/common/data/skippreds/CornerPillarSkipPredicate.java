package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class CornerPillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);

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

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side != dir && side != dir.rotateYCCW()) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && dir == adjDir))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if ((adjTop && side == Direction.DOWN) || (!adjTop && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
        if (side == dir && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir.rotateYCCW());
        }

        if (side == dir.rotateYCCW() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }

        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

        if ((adjTop && side == Direction.UP) || (!adjTop && side == Direction.DOWN))
        {
            if (adjShape == StairsShape.OUTER_LEFT)
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            if (adjShape == StairsShape.OUTER_RIGHT)
            {
                return dir.rotateYCCW() == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL)
        {
            if ((side == dir.rotateYCCW() || side == dir) && adjDir == dir)
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