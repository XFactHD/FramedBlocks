package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatSlopeSlabCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        if (top == topHalf && ((topHalf && side == Direction.UP) || (!topHalf && side == Direction.DOWN)))
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            return switch (blockType)
            {
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private boolean testAgainstFlatSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop != top || adjTopHalf != topHalf) { return false; }

        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private boolean testAgainstFlatInnerSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop != top || adjTopHalf != topHalf) { return false; }

        if (adjDir == dir && (side == dir  || side == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop != top || adjTopHalf != topHalf) { return false; }

        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, adjDir);
        }

        return false;
    }

    private boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (side == dir && ((top && adjDir == dir.getClockWise()) || (!top && adjDir == dir.getCounterClockWise())))
        {
            Direction camoSide = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }
        else if (side == dir.getCounterClockWise() && ((top && adjDir == dir.getOpposite()) || (!top && adjDir == dir)))
        {
            Direction camoSide = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        if (top == topHalf) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side == dir && ((top && adjDir == dir.getClockWise()) || (!top && adjDir == dir.getCounterClockWise())))
        {
            Direction camoSide = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }
        else if (side == dir.getCounterClockWise() && ((top && adjDir == dir.getOpposite()) || (!top && adjDir == dir)))
        {
            Direction camoSide = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private boolean testAgainstElevatedDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (top != topHalf || adjTop == topHalf) { return false; }

        if ((side == dir && adjDir == dir.getClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private boolean testAgainstFlatDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        boolean sameTop = adjTop == top;
        if (side == dir && ((sameTop && adjDir == dir) || (!sameTop && adjDir == dir.getClockWise())))
        {
            Direction camoSide = sameTop ? adjDir : side.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }
        else if (side == dir.getCounterClockWise() && ((sameTop && adjDir == dir) || (!sameTop && adjDir == dir.getCounterClockWise())))
        {
            Direction camoSide = sameTop ? adjDir : side.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }
}
