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

public final class PanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir) { return SideSkipPredicate.CTM.test(level, pos, state, adjState, side); }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, dir, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(level, pos, dir, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, dir, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, dir, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, adjState, side);
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(level, pos, dir, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(level, pos, dir, adjState, side);
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(level, pos, dir, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(level, pos, dir, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        return dir == adjState.getValue(FramedProperties.FACING_HOR) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);
        return (dir == adjDir || dir == adjDir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if (adjDir != dir) { return false; }

        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop))
        {
            return adjShape == StairsShape.STRAIGHT && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (dir.getAxis() != adjDir.getAxis()) { return false; }

        if ((side == Direction.UP && adjTop) || (side == Direction.DOWN && !adjTop))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjType == StairsType.VERTICAL && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if ((side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() == dir.getAxis()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if ((adjRight && adjDir == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getClockWise()))
        {
            if (Utils.isY(side))
            {
                return (side == Direction.DOWN) == adjTop && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else
            {
                return (side == adjDir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() == dir.getAxis() || side != adjRotDir) { return false; }

        if ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() == dir.getAxis()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);

        return adjDir == dir && adjRotDir == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() == dir.getAxis() || side.getAxis() != adjRotDir.getAxis()) { return false; }

        if ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);

        if (side.getAxis() == dir.getAxis() || side.getAxis() != adjRotDir.getAxis()) { return false; }

        if ((adjDir == dir && side == adjRotDir.getOpposite()) || (adjDir == dir.getOpposite() && side == adjRotDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side.getAxis() == dir.getAxis()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        Direction adjRotDir = adjState.getValue(PropertyHolder.ROTATION).withFacing(adjDir);

        if (adjRotDir != side.getOpposite()) { return false; }

        return adjDir.getAxis() == dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if ((!adjFront && adjDir != dir) || (adjFront && adjDir != dir.getOpposite())) { return false; }

        if (FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        if (side == dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if ((!adjFront && adjDir != dir) || (adjFront && adjDir != dir.getOpposite())) { return false; }

        if (FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }
}