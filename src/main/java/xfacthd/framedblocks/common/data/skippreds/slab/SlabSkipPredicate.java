package xfacthd.framedblocks.common.data.skippreds.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

public final class SlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (Utils.isY(side))
        {
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (type)
            {
                case FRAMED_SLAB -> testAgainstSlab(
                        top, adjState, side
                );
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(
                        top, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstEdge(
                        top, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        top, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        top, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        top, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        top, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        top, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        top, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(
                        top, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(
                        top, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        top, adjState, side
                );
                case FRAMED_STACKED_SLOPE_SLAB -> testAgainstStackedSlopeSlab(
                        top, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER -> testAgainstFlatStackedSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatStackedInnerSlopeSlabCorner(
                        top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS, FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfStairs(
                        top, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        top, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        top, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        top, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlab(boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(top, side).isEqualTo(getHalfDir(adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlab(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(top, states.getA(), side) ||
               testAgainstSlab(top, states.getB(), side);
    }

    private static boolean testAgainstEdge(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(top, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlab(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(top, states.getA(), side) ||
               testAgainstEdge(top, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(top, states.getA(), side) ||
               testAgainstEdge(top, states.getB(), side);
    }

    private static boolean testAgainstStairs(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return getHalfDir(top, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    private static boolean testAgainstDoubleStairs(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(top, states.getA(), side) ||
               testAgainstEdge(top, states.getB(), side);
    }

    private static boolean testAgainstSlopeSlab(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(top, side).isEqualTo(SlopeSlabSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    private static boolean testAgainstElevatedSlopeSlab(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(top, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopeSlab(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(top, states.getA(), side) ||
               testAgainstSlopeSlab(top, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(top, states.getA(), side) ||
               testAgainstSlopeSlab(top, states.getB(), side);
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(top, states.getA(), side) ||
               testAgainstSlopeSlab(top, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopeSlab(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(top, states.getA(), side) ||
               testAgainstSlopeSlab(top, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(top, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(top, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(top, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(top, states.getA(), side);
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(top, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(top, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(top, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopeSlabCorner(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(top, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(top, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(top, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalDividedStairs(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(top, states.getB(), side);
    }

    private static boolean testAgainstDividedSlope(boolean top, BlockState adjState, Direction side)
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) != SlopeType.HORIZONTAL)
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(top, states.getB(), side);
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(top, states.getB(), side);
    }

    private static boolean testAgainstSlopedStairs(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(top, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }



    public static HalfDir getHalfDir(boolean top, Direction side)
    {
        if (!Utils.isY(side))
        {
            return HalfDir.fromDirections(
                    side,
                    top ? Direction.UP : Direction.DOWN
            );
        }
        return HalfDir.NULL;
    }
}