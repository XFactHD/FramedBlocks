package xfacthd.framedblocks.common.data.skippreds.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.*;
import xfacthd.framedblocks.common.data.skippreds.slope.HalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;

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
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(level, pos, state, dir, type, adjState, side);
                case FRAMED_PANEL -> testAgainstPanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, state, dir, type, adjState, side);
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(level, pos, state, dir, type, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(level, pos, state, dir, type, adjState, side);
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(level, pos, state, dir, type, adjState, side);
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(level, pos, state, dir, type, adjState, side);
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(level, pos, state, dir, type, adjState, side);
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(level, pos, state, dir, type, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(level, pos, state, dir, type, adjState, side);
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(level, pos, state, dir, type, adjState, side);
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(level, pos, state, dir, type, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getStairDir(dir, type, side).isEqualTo(getStairDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getHalfDir(dir, type, side).isEqualTo(getHalfDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getCornerDir(dir, type, side).isEqualTo(getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstPillar(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getStairDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getHalfDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getCornerDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDividedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        if (getStairDir(dir, type, side).isEqualTo(StairsSkipPredicate.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getHalfDir(dir, type, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getCornerDir(dir, type, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstEdge(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getStairDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getStairDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getHalfDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getCornerDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstHalfStairs(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, type, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstPanel(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getCornerDir(dir, type, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, type, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getCornerDir(dir, type, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstPillar(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, type, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getCornerDir(dir, type, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstEdge(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstEdge(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, type, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, type, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, type, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, type, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, type, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, type, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, type, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getHalfDir(dir, type, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstHalfSlope(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(level, pos, state, dir, type, states.getA(), side) ||
               testAgainstHalfSlope(level, pos, state, dir, type, states.getB(), side);
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, type, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }



    public static TriangleDir getStairDir(Direction dir, StairsType type, Direction side)
    {
        if ((side == Direction.DOWN && type != StairsType.BOTTOM_CORNER) || (side == Direction.UP && type != StairsType.TOP_CORNER))
        {
            return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
        }
        return TriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, StairsType type, Direction side)
    {
        if (type != StairsType.VERTICAL)
        {
            return HalfDir.NULL;
        }

        if (side == dir.getClockWise())
        {
            return HalfDir.fromDirections(side, dir);
        }
        if (side == dir.getOpposite())
        {
            return HalfDir.fromDirections(side, dir.getCounterClockWise());
        }
        return HalfDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, StairsType type, Direction side)
    {
        if (type == StairsType.VERTICAL)
        {
            return CornerDir.NULL;
        }

        Direction dirTwo = type == StairsType.TOP_CORNER ? Direction.DOWN : Direction.UP;
        if (side == dirTwo.getOpposite())
        {
            return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
        }
        if (side == dir.getOpposite())
        {
            return CornerDir.fromDirections(side, dir.getCounterClockWise(), dirTwo);
        }
        if (side == dir.getClockWise())
        {
            return CornerDir.fromDirections(side, dir, dirTwo);
        }
        return CornerDir.NULL;
    }
}