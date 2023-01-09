package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

public final class ElevatedSlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if (side == dir || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(level, pos, state, dir, top, adjState, side);
                case FRAMED_SLAB -> testAgainstSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(level, pos, state, dir, top, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, state, dir, top, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, state, dir, top, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, state, dir, top, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS, FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfStairs(level, pos, state, dir, top, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(level, pos, state, dir, top, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(level, pos, state, dir, top, adjState, side);
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(level, pos, state, dir, top, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (side.getAxis() == dir.getClockWise().getAxis() && adjDir == dir)
        {
            if (adjTop != top) { return false; }

            Direction camoDir = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
        }
        else if (getHalfDir(dir, top, side).isEqualTo(getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getHalfDir(dir, top, side).isEqualTo(SlopeSlabSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstSlopeSlab(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getHalfDir(dir, top, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            if (adjTop != top) { return false; } //TODO: simplify

            Direction camoDir = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedInnerSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top) { return false; }

        if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }
        return false;
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, states.getA(), side);
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedInnerSlopeSlabCorner(level, pos, state, dir, top, states.getA(), side);
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstSlab(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        if (getHalfDir(dir, top, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlope(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) != SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstSlopedStairs(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }



    public static HalfDir getHalfDir(Direction dir, boolean top, Direction side)
    {
        if (side == dir.getOpposite())
        {
            return HalfDir.fromDirections(
                    side,
                    top ? Direction.DOWN : Direction.UP
            );
        }
        return HalfDir.NULL;
    }
}
