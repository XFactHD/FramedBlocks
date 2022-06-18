package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;

public class HalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            Direction stairFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            Direction baseFace = top ? Direction.UP : Direction.DOWN;

            if (type == BlockType.FRAMED_HALF_STAIRS)
            {
                return testAgainstHalfStairs(level, pos, dir, top, right, stairFace, adjState, side);
            }
            else if (type == BlockType.FRAMED_VERTICAL_STAIRS)
            {
                return testAgainstVerticalStairs(level, pos, dir, top, right, adjState, side);
            }

            if (side == stairFace)
            {
                return switch (type)
                {
                    case FRAMED_STAIRS -> testAgainstStairs(level, pos, top, adjState, side);
                    case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, dir, top, adjState, side);
                    default -> false;
                };
            }
            else
            {
                return switch (type)
                {
                    case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, dir, top, right, baseFace, adjState, side);
                    case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(level, pos, dir, right, baseFace, adjState, side);
                    case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(level, pos, dir, top, right, baseFace, adjState, side);
                    case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, right, baseFace, adjState, side);
                    case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, stairFace, baseFace, adjState, side);
                    case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, top, right, adjState, side);
                    case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, dir, top, right, adjState, side);
                    case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, dir, top, right, adjState, side);
                    case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, dir, top, right, adjState, side);
                    case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, dir, top, right, adjState, side);
                    case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, dir, right, adjState, side);
                    default -> false;
                };
            }
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, Direction stairFace, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side == stairFace)
        {
            return adjDir == dir && adjTop == top && adjRight != right && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (Utils.isY(side))
        {
            return adjDir == dir && adjTop != top && adjRight == right && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir)
        {
            return adjDir == dir.getOpposite() && adjRight != right && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        return top == adjTop && StairsSkipPredicate.isStairSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == top && adjDir == dir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        Direction stairFace = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == stairFace && adjType != StairsType.VERTICAL)
        {
            if ((right && adjDir == dir) || (!right && adjDir == dir.getClockWise()))
            {
                return adjType.isTop() != top && SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else if (side == dir && adjType == StairsType.VERTICAL)
        {
            if ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return (adjTop == top) == (side == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstCornerPillar(BlockGetter level, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side == dir && ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == baseFace.getOpposite() && ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, Direction stairFace, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);

        return adjDir.getAxis() != dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, stairFace, stairFace);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (side != dir.getOpposite() || adjTop != top) { return false; }

        if ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side != adjRot.withFacing(adjDir) || (adjRot.isVertical() && top != (adjRot == Rotation.UP))) { return false; }

        if (!right && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (right && ((adjDir == dir.getClockWise() && !adjFront) || (adjDir == dir.getCounterClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.withFacing(adjDir) != side.getOpposite()) { return false; }

        if (adjRot.isVertical() && top == (adjRot == Rotation.DOWN) && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (!adjRot.isVertical() && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise())))
        {
            return right == (adjRot == Rotation.RIGHT) && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() != adjRot.withFacing(adjDir).getAxis() || (adjRot.isVertical() && top != (side == Direction.UP))) { return false; }

        if (!right && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (right && ((adjDir == dir.getClockWise() && !adjFront) || (adjDir == dir.getCounterClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side.getAxis() != adjRot.withFacing(adjDir).getAxis() || (adjRot.isVertical() && top != (side == Direction.UP))) { return false; }

        if (adjRot.isVertical())
        {
            boolean sameOrientation = top == (adjRot == Rotation.UP);
            if (!right && ((adjDir == dir.getCounterClockWise() && !sameOrientation) || (adjDir == dir.getClockWise() && sameOrientation)))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
            else if (right && ((adjDir == dir.getClockWise() && !sameOrientation) || (adjDir == dir.getCounterClockWise() && sameOrientation)))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else
        {
            if (right == (adjRot == Rotation.RIGHT) && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, boolean right, BlockState adjState, Direction side)
    {
        if (side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if (adjDir == dir || adjDir == dir.getClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, side, right ? dir.getClockWise() : dir.getCounterClockWise());
        }

        return false;
    }
}
