package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class HalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }
        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();

        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);
        boolean right = state.getValue(PropertyHolder.RIGHT);

        Direction stairFace = right ? dir.getClockWise() : dir.getCounterClockWise();
        Direction baseFace = top ? Direction.UP : Direction.DOWN;

        if (adjBlock == BlockType.FRAMED_HALF_STAIRS)
        {
            return testAgainstHalfStairs(world, pos, dir, top, right, stairFace, adjState, side);
        }

        if (side == stairFace)
        {
            if (adjBlock == BlockType.FRAMED_STAIRS)
            {
                return testAgainstStairs(world, pos, top, adjState, side);
            }
            else if (adjBlock == BlockType.FRAMED_VERTICAL_STAIRS)
            {
                return testAgainstVerticalStairs(world, pos, dir, top, right, adjState, side);
            }
        }
        else
        {
            switch (adjBlock)
            {
                case FRAMED_SLAB_EDGE: return testAgainstSlabEdge(world, pos, dir, top, right, baseFace, adjState, side);
                case FRAMED_CORNER_PILLAR: return testAgainstCornerPillar(world, pos, dir, right, baseFace, adjState, side);
                case FRAMED_SLAB_CORNER: return testAgainstSlabCorner(world, pos, dir, top, right, baseFace, adjState, side);
                case FRAMED_PANEL: return testAgainstPanel(world, pos, dir, right, baseFace, adjState, side);
                case FRAMED_DOUBLE_PANEL: return testAgainstDoublePanel(world, pos, dir, stairFace, baseFace, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS: return testAgainstVerticalHalfStairs(world, pos, dir, top, right, adjState, side);
                case FRAMED_SLOPE_PANEL: return testAgainstSlopePanel(world, pos, dir, top, right, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL: return testAgainstExtendedSlopePanel(world, pos, dir, top, right, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL: return testAgainstDoubleSlopePanel(world, pos, dir, top, right, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL: return testAgainstInverseDoubleSlopePanel(world, pos, dir, top, right, adjState, side);
            }
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, Direction stairFace, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side == stairFace)
        {
            return adjDir == dir && adjTop == top && adjRight != right && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side.getAxis() == Direction.Axis.Y)
        {
            return adjDir == dir && adjTop != top && adjRight == right && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == dir)
        {
            return adjDir == dir.getOpposite() && adjRight != right && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(StairsBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairsBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairsBlock.HALF) == Half.TOP;

        return top == adjTop && StairsSkipPredicate.isStairSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((right && adjDir == dir) || (!right && adjDir == dir.getClockWise()))
        {
            return adjType.isTop() != top && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return (adjTop == top) == (side == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstCornerPillar(IBlockReader world, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (side == dir && ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == baseFace.getOpposite() && ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir))
        {
            return adjTop == top && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, Direction stairFace, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);

        return adjDir.getAxis() != dir.getAxis() && SideSkipPredicate.compareState(world, pos, side, stairFace);
    }

    private static boolean testAgainstVerticalHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (side != dir.getOpposite() || adjTop != top) { return false; }

        if ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side != adjRot.withFacing(adjDir) || (adjRot.isVertical() && top != (adjRot == Rotation.UP))) { return false; }

        if (!right && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (right && ((adjDir == dir.getClockWise() && !adjFront) || (adjDir == dir.getCounterClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.withFacing(adjDir) != side.getOpposite()) { return false; }

        if (adjRot.isVertical() && top == (adjRot == Rotation.DOWN) && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (!adjRot.isVertical() && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise())))
        {
            return right == (adjRot == Rotation.RIGHT) && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() != adjRot.withFacing(adjDir).getAxis() || (adjRot.isVertical() && top != (side == Direction.UP))) { return false; }

        if (!right && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (right && ((adjDir == dir.getClockWise() && !adjFront) || (adjDir == dir.getCounterClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side.getAxis() != adjRot.withFacing(adjDir).getAxis() || (adjRot.isVertical() && top != (side == Direction.UP))) { return false; }

        if (adjRot.isVertical())
        {
            boolean sameOrientation = top == (adjRot == Rotation.UP);
            if (!right && ((adjDir == dir.getCounterClockWise() && !sameOrientation) || (adjDir == dir.getClockWise() && sameOrientation)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (right && ((adjDir == dir.getClockWise() && !sameOrientation) || (adjDir == dir.getCounterClockWise() && sameOrientation)))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else
        {
            if (right == (adjRot == Rotation.RIGHT) && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }

        return false;
    }
}
