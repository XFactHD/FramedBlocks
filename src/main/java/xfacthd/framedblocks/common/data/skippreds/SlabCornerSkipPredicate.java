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

public class SlabCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(world, pos, dir, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(world, pos, dir, top, adjState, side);
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

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
        {
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        if ((side == Direction.DOWN && !top && adjTop) || (side == Direction.UP && top && !adjTop))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir))
        {
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

        if (top != adjTop) { return false; }

        if (adjShape == StairsShape.OUTER_LEFT)
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }
        if (adjShape == StairsShape.OUTER_RIGHT)
        {
            return dir.rotateYCCW() == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

        if (adjType.isTop() == top || dir != adjDir) { return false; }

        if (side.getAxis() == Direction.Axis.Y || side == dir || side == dir.rotateYCCW())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }
}