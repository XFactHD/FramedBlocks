package xfacthd.framedblocks.common.data.skippreds.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
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
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            return switch (blockType)
            {
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(dir, type, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(dir, type, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(dir, type, adjState, side);
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(dir, type, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(dir, type, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(dir, type, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(dir, type, adjState, side);
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(dir, type, adjState, side);
                case FRAMED_PANEL -> testAgainstPanel(dir, type, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(dir, type, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstCorner(dir, type, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(dir, type, adjState, side);
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(dir, type, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(dir, type, adjState, side);
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(dir, type, adjState, side);
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(dir, type, adjState, side);
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(dir, type, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(dir, type, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(dir, type, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(dir, type, adjState, side);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(dir, type, adjState, side);
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(dir, type, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(dir, type, adjState, side);
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(dir, type, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(dir, type, adjState, side);
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(dir, type, adjState, side);
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(dir, type, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getStairDir(dir, type, side).isEqualTo(getStairDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, type, side).isEqualTo(getHalfDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, type, side).isEqualTo(getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(dir, type, states.getA(), side) ||
               testAgainstPillar(dir, type, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getStairDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalDividedStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, type, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, type, states.getB(), side);
    }

    private static boolean testAgainstStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        if (getStairDir(dir, type, side).isEqualTo(StairsSkipPredicate.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, type, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, type, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, type, states.getA(), side) ||
               testAgainstEdge(dir, type, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getStairDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getStairDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, type, states.getA(), side) ||
               testAgainstHalfStairs(dir, type, states.getB(), side);
    }

    private static boolean testAgainstPanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, type, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    private static boolean testAgainstDoublePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, type, states.getA(), side) ||
               testAgainstPanel(dir, type, states.getB(), side);
    }

    private static boolean testAgainstCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, type, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstPillar(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, type, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, type, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, type, states.getA(), side) ||
               testAgainstPillar(dir, type, states.getB(), side);
    }

    private static boolean testAgainstEdge(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, type, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, type, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, type, states.getA(), side) ||
               testAgainstEdge(dir, type, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, type, states.getA(), side) ||
               testAgainstEdge(dir, type, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, type, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, type, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, type, states.getA(), side) ||
               testAgainstSlopePanel(dir, type, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, type, states.getA(), side) ||
               testAgainstSlopePanel(dir, type, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(dir, type, states.getA(), side) ||
               testAgainstSlopePanel(dir, type, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, type, states.getA(), side) ||
               testAgainstSlopePanel(dir, type, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, type, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, type, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, type, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, type, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, type, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, type, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, type, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, type, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, type, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, type, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlope(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, type, states.getA(), side) ||
               testAgainstHalfSlope(dir, type, states.getB(), side);
    }

    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, type, states.getA(), side) ||
               testAgainstHalfSlope(dir, type, states.getB(), side);
    }

    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, StairsType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, type, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
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