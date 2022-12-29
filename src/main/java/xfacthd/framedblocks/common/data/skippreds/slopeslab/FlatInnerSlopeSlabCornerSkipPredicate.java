package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.stairs.SlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slope.VerticalHalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.StairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalHalfStairsSkipPredicate;

public final class FlatInnerSlopeSlabCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(level, pos, dir, topHalf, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(level, pos, dir, top, topHalf, adjState, side);
                case FRAMED_SLAB -> testAgainstSlab(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(level, pos, dir, topHalf, side);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, dir, topHalf, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, dir, topHalf, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS, FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfStairs(level, pos, dir, topHalf, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(level, pos, dir, topHalf, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(level, pos, dir, topHalf, adjState, side);
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(level, pos, dir, topHalf, adjState, side);
                default -> false;
            };
        }
        return false;
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (isSlabSide(dir, side) && isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir.getOpposite());
        }

        if (adjTop != top) { return false; }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir);
        }

        return false;
    }

    private static boolean testAgainstFlatSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop != top || adjTopHalf != topHalf) { return false; }

        if (adjDir == dir && (side == dir.getOpposite() || side == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (isSlabSide(dir, side) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        if (adjTop != top) { return false; }

        if ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir);
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != topHalf) { return false; }

        if (isSlabSide(dir, side) && adjDir == side)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (isSlabSide(dir, side) && (adjDir == side || adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        if (side == dir.getOpposite() && ((top && adjDir == dir.getClockWise()) || (!top && adjDir == dir.getCounterClockWise())))
        {
            Direction camoDir = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoDir);
        }
        else if (side == dir.getClockWise() && ((top && adjDir == dir.getOpposite()) || (!top && adjDir == dir)))
        {
            Direction camoDir = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoDir);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (isSlabSide(dir, side) && ((topHalf && adjDir == side.getOpposite()) || (!topHalf && adjDir == side)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        if (top == topHalf) { return false; }

        if (side == dir.getOpposite() && ((top && adjDir == dir.getClockWise()) || (!top && adjDir == dir.getCounterClockWise())))
        {
            Direction camoSide = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }
        else if (side == dir.getClockWise() && ((top && adjDir == dir.getOpposite()) || (!top && adjDir == dir)))
        {
            Direction camoSide = top ? adjDir.getOpposite() : adjDir;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (isSlabSide(dir, side) && adjDir == side)
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        if (topHalf != top || adjTop == top) { return false; }

        if ((side == dir.getOpposite() && adjDir == dir.getClockWise()) || (side == dir.getClockWise() && adjDir == dir.getOpposite()))
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == topHalf && isSlabSide(dir, side) && FlatElevatedSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTopHalf != topHalf) { return false; }

        if (isSlabSide(dir, side) && isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        boolean sameTop = adjTop == top;
        if (sameTop && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir);
        }
        else if (!sameTop && adjDir == dir.getOpposite() && (side == dir.getClockWise() || side == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == topHalf && isSlabSide(dir, side) && FlatInnerSlopeSlabCornerSkipPredicate.isInverseSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir.getOpposite());
        }

        if (adjTop == topHalf && adjTop != top && (
                (side == dir.getClockWise() && adjDir == dir.getCounterClockWise()) ||
                (side == dir.getOpposite() && adjDir == dir.getClockWise())
        ))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir.getOpposite());
        }
        else if (adjTop == top && adjTop != topHalf && adjDir == dir && (side == dir.getClockWise() || side == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, adjDir);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (FlatInnerSlopeSlabCornerSkipPredicate.isSlabSide(dir, side) && FlatInnerSlopeSlabCornerSkipPredicate.isInverseSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        if (adjTop == top || adjTop == topHalf) { return false; }

        if ((side == dir.getClockWise() && adjDir == dir.getCounterClockWise()) || (side == dir.getOpposite() && adjDir == dir.getClockWise()))
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top && adjTop != topHalf && adjDir == dir.getOpposite() && (side == dir.getClockWise() || side == dir.getOpposite()))
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }

    private static boolean testAgainstSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (topHalf != adjTop || !isSlabSide(dir, side)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, Direction side
    )
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
        return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == topHalf && isSlabSide(dir, side) && adjDir == side.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        if (topHalf == adjTop && isSlabSide(dir, side) && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);

        if (isSlabSide(dir, side) && adjDir == side)
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == topHalf && isSlabSide(dir, side) && VerticalHalfStairsSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType != SlopeType.HORIZONTAL || !isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (VerticalHalfSlopeSkipPredicate.isSlabFace(adjDir, side.getOpposite()))
        {
            Direction camoSide = topHalf ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, dir, camoSide);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == topHalf && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
    }

    private static boolean testAgainstSlopedStairs(BlockGetter level, BlockPos pos, Direction dir, boolean topHalf, BlockState adjState, Direction side)
    {
        if (!isSlabSide(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == topHalf && SlopedStairsSkipPredicate.isSlabFace(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }



    public static boolean isSlabSide(Direction dir, Direction side)
    {
        return side == dir || side == dir.getCounterClockWise();
    }

    /**
     * Check for slab side on a Framed Flat Inverse Double Slope Slab Corner or Framed Flat Elevated Double Slope Slab Corner
     */
    public static boolean isInverseSlabSide(Direction dir, Direction side)
    {
        return isSlabSide(dir.getOpposite(), side);
    }
}
