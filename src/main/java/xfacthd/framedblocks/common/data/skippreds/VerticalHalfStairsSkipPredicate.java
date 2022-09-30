package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalHalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (type)
            {
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, top, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, top, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, dir, top, adjState, side);
                case FRAMED_SLAB -> testAgainstSlab(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(level, pos, dir, top, side);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, dir, top, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(level, pos, dir, top, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, dir, top, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, top, adjState, side);
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(level, pos, dir, top, adjState, side);
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(level, pos, dir, top, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(level, pos, dir, top, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(level, pos, dir, top, adjState, side);
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(level, pos, dir, top, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            if (adjTop == top || adjDir != dir)
            {
                return false;
            }
            return SideSkipPredicate.compareState(level, pos, side);
        }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }
        if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == Direction.DOWN && !top && !adjType.isTop()) || (side == Direction.UP && top && !adjType.isBottom()))
        {
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((side == Direction.DOWN && !top) || (side == Direction.UP && top))
        {
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        if (side == dir || side == dir.getCounterClockWise())
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top) { return false; }

        if (isSlabSide(dir, side) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && adjDir == dir && (side == dir.getOpposite() || side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);

        if (adjTop != top) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            if ((adjDir == dir && adjShape == StairsShape.INNER_LEFT) || (adjDir == dir.getCounterClockWise() && adjShape == StairsShape.INNER_RIGHT))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else if (StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()) && isSlabSide(dir, side))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        return adjDir == side && SideSkipPredicate.compareState(level, pos, side, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjTop != top || adjDir != side) { return false; }

        if ((adjRight && adjDir == dir.getOpposite()) || (!adjRight && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && adjDir == side)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && (adjDir == side || adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((!top && adjDir == side) || (top && adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (adjDir == side)
        {
            Direction camoDir = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf == top && FlatInnerSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, adjDir);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && FlatElevatedSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf == top && FlatInnerSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, adjDir);
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && FlatInnerSlopeSlabCornerSkipPredicate.isInverseSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (FlatInnerSlopeSlabCornerSkipPredicate.isInverseSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
        }

        return false;
    }



    public static boolean isSlabSide(Direction dir, Direction side)
    {
        return side == dir || side == dir.getCounterClockWise();
    }
}
