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

public class PanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
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

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        return dir == adjState.get(PropertyHolder.FACING_HOR) && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
        return (dir == adjDir || dir == adjDir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if ((side == dir.rotateY() && adjDir == dir) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if (adjDir != dir) { return false; }

        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop))
        {
            return adjShape == StairsShape.STRAIGHT && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

        if ((side == dir.rotateYCCW() && adjDir == dir) || (side == dir.rotateY() && adjDir == dir.rotateY()))
        {
            return adjType == StairsType.VERTICAL && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }
}