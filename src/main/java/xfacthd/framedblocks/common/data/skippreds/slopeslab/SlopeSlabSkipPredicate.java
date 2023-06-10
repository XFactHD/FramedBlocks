package xfacthd.framedblocks.common.data.skippreds.slopeslab;

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
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

public final class SlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

            return switch (type)
            {
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_STACKED_SLOPE_SLAB -> testAgainstStackedSlopeSlab(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER -> testAgainstFlatStackedSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatStackedInnerSlopeSlabCorner(
                        level, pos, state, dir, top, topHalf, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS, FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfStairs(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        level, pos, state, dir, topHalf, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        level, pos, state, dir, topHalf, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getTriDir(dir, topHalf, top, side).isEqualTo(getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getHalfDir(dir, topHalf, side).isEqualTo(getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, topHalf, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(level, pos, state, dir, top, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(level, pos, state, dir, top, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getTriDir(dir, topHalf, top, side).isEqualTo(FlatSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getHalfDir(dir, topHalf, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getTriDir(dir, topHalf, top, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, topHalf, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, topHalf, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstSlab(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, topHalf, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        if (getHalfDir(dir, topHalf, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, topHalf, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDividedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) != SlopeType.HORIZONTAL)
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(level, pos, state, dir, topHalf, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, topHalf, states.getB(), side);
    }

    private static boolean testAgainstSlopedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, topHalf, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side)
    {
        if (side.getAxis() == dir.getClockWise().getAxis())
        {
            Direction longEdge = top ? Direction.UP : Direction.DOWN;
            return HalfTriangleDir.fromDirections(longEdge, dir, topHalf == top);
        }
        return HalfTriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, boolean topHalf, Direction side)
    {
        if (side == dir)
        {
            return HalfDir.fromDirections(
                    side,
                    topHalf ? Direction.UP : Direction.DOWN
            );
        }
        return HalfDir.NULL;
    }
}
