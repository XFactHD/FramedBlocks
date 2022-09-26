package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class CornerPillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            return switch (type)
            {
                case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, dir, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstCorner(level, pos, dir, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, dir, adjState, side);
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(level, pos, dir, adjState, side);
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(level, pos, dir, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side != dir && side != dir.getCounterClockWise()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && dir == adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (Utils.isY(side) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        if ((adjTop && side == Direction.DOWN) || (!adjTop && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        return false;
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);
        if (side == dir && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir.getCounterClockWise(), dir.getCounterClockWise());
        }

        if (side == dir.getCounterClockWise() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if ((adjTop && side == Direction.UP) || (!adjTop && side == Direction.DOWN))
        {
            if (adjShape == StairsShape.OUTER_LEFT)
            {
                return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
            if (adjShape == StairsShape.OUTER_RIGHT)
            {
                return dir.getCounterClockWise() == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL)
        {
            if ((side == dir.getCounterClockWise() || side == dir) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else if (Utils.isY(side))
        {
            if ((side == Direction.DOWN) == adjType.isTop() && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (Utils.isY(side) && adjDir == dir.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (adjDir == dir && (side == dir || side == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if ((adjDir == dir.getCounterClockWise() && side == dir.getCounterClockWise()) || (adjDir == dir.getClockWise() && side == dir))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (Utils.isY(side) && (side == Direction.UP) == adjTop)
        {
            if ((adjRight && adjDir == dir.getCounterClockWise()) || (!adjRight && adjDir == dir))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else if ((adjRight && side == dir) || (!adjRight && side == dir.getCounterClockWise()))
        {
            if ((adjRight && adjDir == dir.getOpposite()) || ((!adjRight && adjDir == dir.getClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side != dir && side != dir.getCounterClockWise()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot.isVertical() || side != adjRot.withFacing(adjDir)) { return false; }

        if ((!adjFront && (adjDir == dir || adjDir == dir.getCounterClockWise())) || (adjFront && (adjDir == dir.getOpposite() || adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side != dir && side != dir.getCounterClockWise()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.isVertical()) { return false; }

        if (side == dir && adjDir == dir.getCounterClockWise() && adjRot == HorizontalRotation.LEFT)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir.getCounterClockWise() && adjDir == dir && adjRot == HorizontalRotation.RIGHT)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot.isVertical() || side.getAxis() != adjRot.withFacing(adjDir).getAxis()) { return false; }

        if ((!adjFront && (adjDir == dir || adjDir == dir.getCounterClockWise())) || (adjFront && (adjDir == dir.getOpposite() || adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.isVertical() || side.getAxis() != adjRot.withFacing(adjDir).getAxis()) { return false; }

        if ((side == dir && adjRot == HorizontalRotation.LEFT) || (side == dir.getCounterClockWise() && adjRot == HorizontalRotation.RIGHT))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side != dir && side != dir.getCounterClockWise()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.isVertical()) { return false; }

        if (side == dir && adjDir == dir.getCounterClockWise() && adjRot == HorizontalRotation.LEFT)
        {
            return SideSkipPredicate.compareState(level, pos, side, adjDir, adjDir);
        }
        else if (side == dir && adjDir == dir.getClockWise() && adjRot == HorizontalRotation.RIGHT)
        {
            Direction camoDir = adjDir.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
        }
        else if (side == dir.getCounterClockWise() && adjDir == dir && adjRot == HorizontalRotation.RIGHT)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == dir.getCounterClockWise() && adjDir == dir.getOpposite() && adjRot == HorizontalRotation.LEFT)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (isPanelSide(dir, side) && FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            Direction secondPanelSide = adjFront ? adjDir.getOpposite() : adjDir;
            return isPanelSide(dir, secondPanelSide) && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (!isPanelSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()) && isPanelSide(dir, adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }



    public static boolean isPanelSide(Direction dir, Direction side)
    {
        return side == dir || side == dir.getCounterClockWise();
    }
}