package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.CornerDir;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.slab.PanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slope.HalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

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
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(
                        dir, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstCorner(
                        dir, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        dir, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstPillar(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        if (getHalfDir(dir, side).isEqualTo(getHalfDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, side).isEqualTo(getCornerDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, states.getA(), side) ||
               testAgainstPillar(dir, states.getB(), side);
    }

    private static boolean testAgainstCorner(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstPanel(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    private static boolean testAgainstDoublePanel(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, states.getA(), side) ||
               testAgainstPanel(dir, states.getB(), side);
    }

    private static boolean testAgainstStairs(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        if (getHalfDir(dir, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalStairs(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getHalfDir(dir, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(dir, states.getA(), side) ||
               testAgainstPillar(dir, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getHalfDir(dir, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, states.getA(), side) ||
               testAgainstHalfStairs(dir, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstExtendedSlopePanel(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopePanel(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, states.getA(), side) ||
               testAgainstSlopePanel(dir, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, states.getA(), side) ||
               testAgainstSlopePanel(dir, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(dir, states.getA(), side) ||
               testAgainstSlopePanel(dir, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, states.getA(), side) ||
               testAgainstSlopePanel(dir, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlope(Direction dir, BlockState adjState, Direction side)
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, states.getA(), side) ||
               testAgainstHalfSlope(dir, states.getB(), side);
    }

    private static boolean testAgainstDoubleHalfSlope(Direction dir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, states.getA(), side) ||
               testAgainstHalfSlope(dir, states.getB(), side);
    }

    private static boolean testAgainstVerticalSlopedStairs(Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }



    public static HalfDir getHalfDir(Direction dir, Direction side)
    {
        if (side == dir)
        {
            return HalfDir.fromDirections(side, dir.getCounterClockWise());
        }
        else if (side == dir.getCounterClockWise())
        {
            return HalfDir.fromDirections(side, dir);
        }
        return HalfDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, Direction side)
    {
        if (Utils.isY(side))
        {
            return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
        }
        return CornerDir.NULL;
    }
}