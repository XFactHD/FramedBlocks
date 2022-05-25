package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class VerticalStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

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
        else if (adjState.is(FBContent.blockFramedHalfStairs.get()))
        {
            return testAgainstHalfStairs(world, pos, dir, type, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedVerticalHalfStairs.get()))
        {
            return testAgainstVerticalHalfStairs(world, pos, dir, type, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedSlopePanel.get()))
        {
            return testAgainstSlopePanel(world, pos, dir, type, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedExtendedSlopePanel.get()))
        {
            return testAgainstExtendedSlopePanel(world, pos, dir, type, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedDoubleSlopePanel.get()))
        {
            return testAgainstDoubleSlopePanel(world, pos, dir, type, adjState, side);
        }
        else if (adjState.is(FBContent.blockFramedInverseDoubleSlopePanel.get()))
        {
            return testAgainstInverseDoubleSlopePanel(world, pos, dir, type, adjState, side);
        }

        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((!type.isBottom() && !adjType.isTop() && side == Direction.DOWN) || (!type.isTop() && !adjType.isBottom() && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }

        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjBottom = adjState.getValue(BlockStateProperties.HALF) == Half.BOTTOM;

        if (type == StairsType.VERTICAL && ((side == Direction.UP && !adjBottom) || (side == Direction.DOWN && adjBottom)))
        {
            StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
            if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.getCounterClockWise() && adjShape == StairsShape.INNER_RIGHT))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else
        {
            if (type.isTop() == adjBottom && ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (side == dir.getClockWise() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        if (side == dir.getOpposite() && (adjDir == dir.getCounterClockWise() || adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir.getCounterClockWise());
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side.getAxis() == Direction.Axis.Y || side == dir.getOpposite() || side == dir.getClockWise()) && type.isTop() != adjTop && dir == adjDir)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (type == StairsType.VERTICAL)
        {
            if ((side == dir.getClockWise() || side == dir.getOpposite()) && adjDir == dir)
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
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return adjTop != type.isTop() && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjTop == type.isTop()) { return false; }

        if ((adjRight && adjDir == dir && side == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getCounterClockWise() && side == dir))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) || (side == Direction.UP && type == StairsType.TOP_CORNER) || (side == Direction.DOWN && type == StairsType.BOTTOM_CORNER))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (!Utils.isY(side) || adjDir != dir || adjTop != (side == Direction.DOWN)) { return false; }

        return SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstSlopePanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (!adjFront && ((adjDir == dir && adjRot == Rotation.RIGHT) || (adjDir == dir.getCounterClockWise() && adjRot == Rotation.LEFT)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjFront && ((adjDir == dir.getOpposite() && adjRot == Rotation.LEFT) || (adjDir == dir.getClockWise() && adjRot == Rotation.RIGHT)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == dir.getOpposite() && adjDir == dir.getCounterClockWise() && adjRot == Rotation.RIGHT)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == dir.getClockWise() && adjDir == dir && adjRot == Rotation.LEFT)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot.isVertical()) { return false; }

        if (!adjFront && (adjDir == dir || adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjFront && (adjDir == dir.getOpposite() || adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir.getAxis() == dir.getAxis() && adjRot == Rotation.LEFT)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjDir.getAxis() == dir.getClockWise().getAxis() && adjRot == Rotation.RIGHT)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}