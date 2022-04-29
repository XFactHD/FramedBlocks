package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;

public class StairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(StairBlock.FACING);
            StairsShape shape = state.getValue(StairBlock.SHAPE);
            boolean top = state.getValue(StairBlock.HALF) == Half.TOP;

            return switch (type)
            {
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, shape, top, adjState, side);
                case FRAMED_SLAB -> testAgainstSlab(level, pos, dir, shape, top, adjState, side);
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(level, pos, dir, shape, top, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(level, pos, dir, shape, top, adjState, side);
                case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, shape, top, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, shape, top, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(level, pos, dir, shape, top, adjState, side);
                case FRAMED_SLAB_CORNER -> testAgainstCorner(level, pos, dir, shape, top, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, shape, top, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(level, pos, dir, shape, top, adjState, side);
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, dir, shape, top, adjState, side);
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(level, pos, dir, shape, top, adjState, side);
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(level, pos, dir, shape, top, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, dir, shape, top, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, shape, top, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        if ((isStairSide(shape, dir, side) && isStairSide(adjShape, adjDir, side.getOpposite())) ||
            (isSlabSide(shape, dir, side) && isSlabSide(adjShape, adjDir, side.getOpposite()))
        )
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }

        if (Utils.isY(side) && adjDir == dir && adjShape == shape && adjTop != top)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if (top != adjTop) { return false; }
        if (!isSlabSide(shape, dir, side)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, Direction side)
    {
        if (!isSlabSide(shape, dir, side)) { return false; }

        return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstEdge(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if (top != adjTop) { return false; }

        if (adjDir == side.getOpposite())
        {
            if (!isSlabSide(shape, dir, side)) { return false; }

            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if ((top && side == Direction.DOWN) || (!top && side == Direction.UP))
        {
            if (shape != StairsShape.STRAIGHT) { return false; }

            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.STRAIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        return dir == adjDir && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.STRAIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (dir == adjDir || dir.getOpposite() == adjDir)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstPillar(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if ((shape == StairsShape.OUTER_LEFT && dir == adjDir) || (shape == StairsShape.OUTER_RIGHT && dir.getClockWise() == adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        if ((shape == StairsShape.OUTER_LEFT && dir == adjDir) || (shape == StairsShape.OUTER_RIGHT && dir.getClockWise() == adjDir))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL)
        {
            if (((side == Direction.DOWN && top) || (side == Direction.UP && !top)) &&
                ((shape == StairsShape.INNER_LEFT && adjDir == dir) || (shape == StairsShape.INNER_RIGHT && adjDir == dir.getClockWise()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        else if (adjType.isTop() != top)
        {
            if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (!isStairSide(shape, dir, side) || top != adjTop) { return false; }

        Direction adjStairFace = adjRight ? adjDir.getClockWise() : adjDir.getCounterClockWise();
        return adjStairFace == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return adjDir == side.getOpposite() && adjTopHalf == top && isSlabSide(shape, dir, side) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjDir == side && adjTop == top && isSlabSide(shape, dir, side) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (adjDir != side && adjDir != side.getOpposite()) { return false; }

        return adjTopHalf == top && isSlabSide(shape, dir, side) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return ((adjDir == side && !top) || (adjDir == side.getOpposite() && top)) && isSlabSide(shape, dir, side) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (!isSlabSide(shape, dir, side) || adjTop != top) { return false; }

        if (adjDir == side.getOpposite() || adjDir == side.getCounterClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }



    public static boolean isStairSide(StairsShape shape, Direction dir, Direction side)
    {
        if (shape == StairsShape.STRAIGHT) { return side == dir.getClockWise() || side == dir.getCounterClockWise(); }

        if (shape == StairsShape.INNER_LEFT) { return side == dir.getOpposite() || side == dir.getClockWise(); }
        if (shape == StairsShape.INNER_RIGHT) { return side == dir.getOpposite() || side == dir.getCounterClockWise(); }

        if (shape == StairsShape.OUTER_LEFT)  { return side == dir || side == dir.getCounterClockWise(); }
        if (shape == StairsShape.OUTER_RIGHT) { return side == dir || side == dir.getClockWise(); }

        return false;
    }

    public static boolean isSlabSide(StairsShape shape, Direction dir, Direction side)
    {
        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT) { return false; }

        if (shape == StairsShape.STRAIGHT) { return side == dir.getOpposite(); }

        if (shape == StairsShape.OUTER_LEFT)  { return side == dir.getOpposite() || side == dir.getClockWise(); }
        if (shape == StairsShape.OUTER_RIGHT) { return side == dir.getOpposite() || side == dir.getCounterClockWise(); }

        return false;
    }
}