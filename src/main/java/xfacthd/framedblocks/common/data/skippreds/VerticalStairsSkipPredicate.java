package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.api.util.*;

public final class VerticalStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if (type == StairsType.VERTICAL && (side == dir || side == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            return switch (blockType)
            {
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, dir, type, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, type, adjState, side);
                case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, type, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstCorner(level, pos, dir, type, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, dir, type, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(level, pos, dir, type, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, type, adjState, side);
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, dir, type, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, dir, type, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, dir, type, adjState, side);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(level, pos, dir, type, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(level, pos, dir, type, adjState, side);
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(level, pos, dir, type, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(level, pos, dir, type, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(level, pos, dir, type, adjState, side);
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(level, pos, dir, type, adjState, side);
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(level, pos, dir, type, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((!type.isBottom() && !adjType.isTop() && side == Direction.DOWN) || (!type.isTop() && !adjType.isBottom() && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((!type.isBottom() && side == Direction.DOWN) || (!type.isTop() && side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir);
        }

        if (dir == adjDir.getOpposite() && (side == dir.getOpposite() || side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjBottom = adjState.getValue(BlockStateProperties.HALF) == Half.BOTTOM;

        if (type == StairsType.VERTICAL && ((side == Direction.UP && !adjBottom) || (side == Direction.DOWN && adjBottom)))
        {
            StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
            if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.getCounterClockWise() && adjShape == StairsShape.INNER_RIGHT))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else
        {
            if (type.isTop() == adjBottom && ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        return false;
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);
        if (side == dir.getClockWise() && (adjDir == dir || adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        if (side == dir.getOpposite() && (adjDir == dir.getCounterClockWise() || adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir.getCounterClockWise(), dir.getCounterClockWise());
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((Utils.isY(side) || side == dir.getOpposite() || side == dir.getClockWise()) && type.isTop() != adjTop && dir == adjDir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if (type == StairsType.VERTICAL)
        {
            if ((side == dir.getClockWise() || side == dir.getOpposite()) && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else if (Utils.isY(side))
        {
            if ((side == Direction.UP) == type.isTop() && adjDir == dir)
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        return false;
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type == StairsType.VERTICAL || Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return adjTop != type.isTop() && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (type == StairsType.VERTICAL)
        {
            if ((!adjRight && adjDir == dir && side == dir.getOpposite()) || (adjRight && adjDir == dir.getCounterClockWise() && side == dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }
        else if (adjTop != type.isTop())
        {
            if ((adjRight && adjDir == dir && side == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getCounterClockWise() && side == dir))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) || (side == Direction.UP && type == StairsType.TOP_CORNER) || (side == Direction.DOWN && type == StairsType.BOTTOM_CORNER))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir != dir || adjTop != (side == Direction.DOWN)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
    }

    private static boolean testAgainstSlopePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (!adjFront && ((adjDir == dir && adjRot == HorizontalRotation.RIGHT) || (adjDir == dir.getCounterClockWise() && adjRot == HorizontalRotation.LEFT)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (adjFront && ((adjDir == dir.getOpposite() && adjRot == HorizontalRotation.LEFT) || (adjDir == dir.getClockWise() && adjRot == HorizontalRotation.RIGHT)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == dir.getOpposite() && adjDir == dir.getCounterClockWise() && adjRot == HorizontalRotation.RIGHT)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (side == dir.getClockWise() && adjDir == dir && adjRot == HorizontalRotation.LEFT)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (adjRot.isVertical()) { return false; }

        if (!adjFront && (adjDir == dir || adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (adjFront && (adjDir == dir.getOpposite() || adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir.getAxis() == dir.getAxis() && adjRot == HorizontalRotation.LEFT)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (adjDir.getAxis() == dir.getClockWise().getAxis() && adjRot == HorizontalRotation.RIGHT)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        if (type != StairsType.VERTICAL || (side != dir.getOpposite() && side != dir.getClockWise())) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.isVertical() || adjDir.getAxis() != side.getClockWise().getAxis()) { return false; }

        if (side == dir.getOpposite() && adjDir.getAxis() == dir.getClockWise().getAxis())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir.getCounterClockWise());
        }
        else if (side == dir.getClockWise() && adjDir.getAxis() == dir.getAxis())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir.getClockWise() && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (side == dir.getOpposite() && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir);
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir.getClockWise() && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (side == dir.getOpposite() && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir.getClockWise() && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir.getClockWise() && adjDir.getAxis() == dir.getAxis())
        {
            Direction camoDir = adjDir == dir ? adjDir : adjDir.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, dir, camoDir);
        }
        else if (side == dir.getOpposite() && adjDir.getAxis() == dir.getCounterClockWise().getAxis())
        {
            Direction camoDir = adjDir.getCounterClockWise() == dir ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoDir);
        }

        return false;
    }

    private static boolean testAgainstHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if ((!adjRight && adjDir == dir && side == dir.getOpposite()) || (adjRight && adjDir == dir.getCounterClockWise() && side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (type != StairsType.VERTICAL || adjType == SlopeType.HORIZONTAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (adjDir == dir && side == dir.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir.getCounterClockWise());
        }
        else if (adjDir == dir.getCounterClockWise() && side == dir.getClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side == dir.getOpposite() && ((!adjRight && adjDir == dir) || (adjRight && adjDir == dir.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }
        else if (side == dir.getClockWise() && ((!adjRight && adjDir == dir.getClockWise()) || (adjRight && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (type != StairsType.VERTICAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, side.getOpposite()))
        {
            return false;
        }

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }
}