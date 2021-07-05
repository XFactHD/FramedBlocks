package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class SlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (side.getAxis() == Direction.Axis.Y) { return SideSkipPredicate.CTM.test(world, pos, state, adjState, side); }

        boolean top = state.getValue(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlab.get())
        {
            return testAgainstSlab(world, pos, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            return testAgainstDoubleSlab(world, pos, top, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(world, pos, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(world, pos, top, adjState, side);
        }

        return false;
    }

    private boolean testAgainstSlab(IBlockReader world, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        if (top != adjState.getValue(PropertyHolder.TOP)) { return false; }

        return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, boolean top, Direction side)
    {
        Direction face = top ? Direction.UP : Direction.DOWN;
        return SideSkipPredicate.compareState(world, pos, side, face);
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        if (top != adjState.getValue(PropertyHolder.TOP)) { return false; }
        if (adjState.getValue(PropertyHolder.FACING_HOR) != side.getOpposite()) { return false; }

        return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (top == adjTop && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }
}