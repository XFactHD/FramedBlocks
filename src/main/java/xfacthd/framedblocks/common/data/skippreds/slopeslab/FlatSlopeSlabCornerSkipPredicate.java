package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;

public final class FlatSlopeSlabCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        if (top == topHalf && ((topHalf && side == Direction.UP) || (!topHalf && side == Direction.DOWN)))
        {
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            return switch (blockType)
            {
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_STACKED_SLOPE_SLAB -> testAgainstStackedSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER -> testAgainstFlatStackedSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatStackedInnerSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getTriDir(dir, topHalf, top, side).isEqualTo(getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getTriDir(dir, topHalf, top, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getTriDir(dir, topHalf, top, side).isEqualTo(SlopeSlabSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, top, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, top, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side)
    {
        if (side == dir || side == dir.getCounterClockWise())
        {
            Direction longEdge = top ? Direction.UP : Direction.DOWN;
            Direction shortEdge = side == dir ? dir.getCounterClockWise() : dir;
            return HalfTriangleDir.fromDirections(longEdge, shortEdge, topHalf == top);
        }
        return HalfTriangleDir.NULL;
    }
}
