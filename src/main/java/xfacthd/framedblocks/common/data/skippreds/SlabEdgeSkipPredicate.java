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

public class SlabEdgeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlab.get())
        {
            return testAgainstSlab(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            return testAgainstDoubleSlab(world, pos, dir, top, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(world, pos, dir, top, adjState, side);
        }
        return false;
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (side == dir && adjDir == side.getOpposite())
        {
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == dir.rotateY() || side == dir.rotateYCCW())
        {
            return dir == adjDir && top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side.getAxis() == Direction.Axis.Y && dir == adjDir)
        {
            return top != adjTop && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private boolean testAgainstSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side != dir || top != adjState.get(PropertyHolder.TOP)) { return false; }

        return SideSkipPredicate.compareState(world, pos, side);
    }

    private boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        if (side != dir) { return false; }

        Direction face = top ? Direction.UP : Direction.DOWN;
        return SideSkipPredicate.compareState(world, pos, side, face);
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if ((side == dir.rotateY() && adjDir == dir) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
        {
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
        if (dir != adjDir && dir != adjDir.getOpposite()) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

        if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            if (adjShape != StairsShape.STRAIGHT || dir != adjDir) { return false; }
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (top == adjTop && side == dir && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL) { return false; }
        if (((side == dir.rotateYCCW() && adjDir == dir) || (side == dir.rotateY() && adjDir == dir.rotateY())))
        {
            return top != adjType.isTop() && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }
}