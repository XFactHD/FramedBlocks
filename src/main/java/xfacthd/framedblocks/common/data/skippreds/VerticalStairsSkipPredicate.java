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

public class VerticalStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }

        Direction dir = state.get(PropertyHolder.FACING_HOR);
        StairsType type = state.get(PropertyHolder.STAIRS_TYPE);

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(world, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(world, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(world, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(world, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(world, pos, dir, type, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get() && type != StairsType.VERTICAL && side.getAxis() != Direction.Axis.Y)
        {
            return testAgainstEdge(world, pos, dir, type, adjState, side);
        }

        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);

        if ((!type.isBottom() && !adjType.isTop() && side == Direction.DOWN) || (!type.isTop() && !adjType.isBottom() && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }

        if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjBottom = adjState.get(BlockStateProperties.HALF) == Half.BOTTOM;

        if (type == StairsType.VERTICAL && ((side == Direction.UP && !adjBottom) || (side == Direction.DOWN && adjBottom)))
        {
            StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
            if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.rotateYCCW() && adjShape == StairsShape.INNER_RIGHT))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else
        {
            if (type.isTop() == adjBottom && ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
        if (side == dir.rotateY() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        if (side == dir.getOpposite() && (adjDir == dir.rotateYCCW() || adjDir == dir.rotateY()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir.rotateYCCW());
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if ((side.getAxis() == Direction.Axis.Y || side == dir.getOpposite() || side == dir.rotateY()) && type.isTop() != adjTop && dir == adjDir)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if (type == StairsType.VERTICAL)
        {
            if ((side == dir.rotateY() || side == dir.getOpposite()) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else if (side.getAxis() == Direction.Axis.Y)
        {
            if ((side == Direction.UP) == type.isTop() && adjDir == dir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW()))
        {
            return adjTop != type.isTop() && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }
}