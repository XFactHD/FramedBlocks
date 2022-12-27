package xfacthd.framedblocks.common.data.skippreds.slope;

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
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.stairs.StairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalHalfStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.FlatElevatedSlopeSlabCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.FlatInnerSlopeSlabCornerSkipPredicate;

public final class SlopedStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if (side == dir || side == dir.getCounterClockWise() || (!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            return switch (blockType)
            {
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_SLOPE -> testAgainstSlope(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(
                        level, pos, dir, top, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(
                        level, pos, dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        level, pos, dir, top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlopedStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && isSlabFace(dir, side) && isSlabFace(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjTop != top && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top) { return false; }

        if (((!top && side == Direction.UP) || (top && side == Direction.DOWN)) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (isSlabFace(dir, side) && VerticalHalfSlopeSkipPredicate.isSlabFace(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top) { return false; }

        if (((!top && side == Direction.UP) || (top && side == Direction.DOWN)) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }
        else if (isSlabFace(dir, side) && VerticalHalfSlopeSkipPredicate.isSlabFace(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        if (adjType != SlopeType.HORIZONTAL) { return false; }

        if (((!top && side == Direction.UP) || (top && side == Direction.DOWN)) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (isSlabFace(dir, side) && VerticalHalfSlopeSkipPredicate.isSlabFace(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType != SlopeType.HORIZONTAL || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlope(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType != SlopeType.HORIZONTAL || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return adjDir.getAxis() == dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, side, dir);
    }

    private static boolean testAgainstCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        if (!adjType.isHorizontal() || (adjType.isTop() != top) || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        boolean adjRight = adjType.isRight();
        if ((!adjRight && adjDir == dir) || (adjRight && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInnerCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        if (!adjType.isHorizontal() || (adjType.isTop() == top) || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        boolean adjRight = adjType.isRight();
        if ((!adjRight && adjDir == dir) || (adjRight && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        if (!adjType.isHorizontal() || (adjType.isTop() == top) || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        boolean adjRight = adjType.isRight();
        if (!adjRight && adjDir.getAxis() == dir.getAxis())
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }
        else if (adjRight && adjDir.getAxis() == dir.getClockWise().getAxis())
        {
            Direction camoSide = dir.getCounterClockWise();
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }

    private static boolean testAgainstThreewayCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if ((!top && side != Direction.UP) || (top && side != Direction.DOWN)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjDir == dir && adjTop == top && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstInnerThreewayCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if ((!top && side != Direction.UP) || (top && side != Direction.DOWN)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjDir == dir && adjTop != top && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleThreewayCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if ((!top && side != Direction.UP) || (top && side != Direction.DOWN)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir.getAxis() == dir.getAxis() && adjTop != top)
        {
            return SideSkipPredicate.compareState(level, pos, side, side, dir);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == top && adjDir == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, Direction side
    )
    {
        Direction camoSide = top ? Direction.UP : Direction.DOWN;
        return isSlabFace(dir, side) && SideSkipPredicate.compareState(level, pos, side, side, camoSide);
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        if (adjTop == top && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        Direction camoSide = top ? Direction.UP : Direction.DOWN;
        return adjDir == side && SideSkipPredicate.compareState(level, pos, side, side, camoSide);
    }

    private static boolean testAgainstVerticalHalfStairs(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && VerticalHalfStairsSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        return adjTop == top && adjDir == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstElevatedSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == top && adjDir == side && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        return adjTop == top && adjDir.getAxis() == side.getAxis() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((!top && adjDir == side) || (top && adjDir == side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        Direction camoSide = top ? Direction.UP : Direction.DOWN;
        return adjDir == side && SideSkipPredicate.compareState(level, pos, side, side, camoSide);
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && FlatInnerSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && FlatElevatedSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjTop == top && FlatInnerSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && FlatInnerSlopeSlabCornerSkipPredicate.isInverseSlabSide(adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!isSlabFace(dir, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (FlatElevatedSlopeSlabCornerSkipPredicate.isSlabSide(adjDir, side.getOpposite()))
        {
            Direction camoSide = top ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(level, pos, side, side, camoSide);
        }

        return false;
    }



    public static boolean isSlabFace(Direction dir, Direction side)
    {
        return side == dir.getOpposite() || side == dir.getClockWise();
    }
}
