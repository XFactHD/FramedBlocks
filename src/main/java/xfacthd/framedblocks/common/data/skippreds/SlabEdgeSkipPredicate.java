package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class SlabEdgeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }
        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);

        switch (adjBlock)
        {
            case FRAMED_SLAB_EDGE: return testAgainstEdge(world, pos, dir, top, adjState, side);
            case FRAMED_SLAB: return testAgainstSlab(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_SLAB: return testAgainstDoubleSlab(world, pos, dir, top, side);
            case FRAMED_SLAB_CORNER: return testAgainstCorner(world, pos, dir, top, adjState, side);
            case FRAMED_PANEL: return testAgainstPanel(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_PANEL: return testAgainstDoublePanel(world, pos, dir, top, adjState, side);
            case FRAMED_STAIRS: return testAgainstStairs(world, pos, dir, top, adjState, side);
            case FRAMED_VERTICAL_STAIRS: return testAgainstVerticalStairs(world, pos, dir, top, adjState, side);
            case FRAMED_HALF_STAIRS: return testAgainstHalfStairs(world, pos, dir, top, adjState, side);
            case FRAMED_SLOPE_SLAB: return testAgainstSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_ELEVATED_SLOPE_SLAB: return testAgainstElevatedSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_SLOPE_SLAB: return testAgainstDoubleSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_INV_DOUBLE_SLOPE_SLAB: return testAgainstInverseDoubleSlopeSlab(world, pos, dir, top, adjState, side);
            case FRAMED_VERTICAL_HALF_STAIRS: return testAgainstVerticalHalfStairs(world, pos, dir, top, adjState, side);
            case FRAMED_SLOPE_PANEL: return testAgainstSlopePanel(world, pos, dir, top, adjState, side);
            case FRAMED_EXTENDED_SLOPE_PANEL: return testAgainstExtendedSlopePanel(world, pos, dir, top, adjState, side);
            case FRAMED_DOUBLE_SLOPE_PANEL: return testAgainstDoubleSlopePanel(world, pos, dir, top, adjState, side);
            case FRAMED_INV_DOUBLE_SLOPE_PANEL: return testAgainstInverseDoubleSlopePanel(world, pos, dir, top, adjState, side);
            default: return false;
        }
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (side == dir && adjDir == side.getOpposite())
        {
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return dir == adjDir && top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side.getAxis() == Direction.Axis.Y && dir == adjDir)
        {
            return top != adjTop && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private boolean testAgainstSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side != dir || top != adjState.getValue(PropertyHolder.TOP)) { return false; }

        return SideSkipPredicate.compareState(world, pos, side);
    }

    private boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        if (side != dir) { return false; }

        Direction face = top ? Direction.UP : Direction.DOWN;
        return SideSkipPredicate.compareState(world, pos, side, face);
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        {
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        if (dir != adjDir) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (side.getAxis() != Direction.Axis.Y) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);
        if (dir != adjDir && dir != adjDir.getOpposite()) { return false; }

        if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            if (adjShape != StairsShape.STRAIGHT || dir != adjDir) { return false; }
            return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (top == adjTop && side == dir && StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL) { return false; }
        if (((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise())))
        {
            return top != adjType.isTop() && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if ((adjRight && adjDir == dir.getCounterClockWise()) || (!adjRight && adjDir == dir.getClockWise()))
        {
            if (side.getAxis() == Direction.Axis.Y && (side == Direction.DOWN) == adjTop && adjTop != top)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }

            return side == adjDir && adjTop == top && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (side != dir) { return false; }

        return adjDir == dir.getOpposite() && adjTopHalf == top && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstElevatedSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (side != dir) { return false; }

        return adjDir == dir && adjTop == top && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (side != dir) { return false; }

        return (adjDir == dir || adjDir == dir.getOpposite()) && adjTopHalf == top && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);

        if (side != dir) { return false; }

        return ((adjDir == dir && !top) || (adjDir == dir.getOpposite() && top)) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstVerticalHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (Utils.isY(side) || side == dir.getOpposite() || adjTop != top) { return false; }

        if (side == dir && (adjDir == side.getOpposite() || adjDir == side.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        if ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (!adjRot.isVertical() || side != adjRot.withFacing(adjDir)) { return false; }

        if (top == (adjRot == Rotation.UP) && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) || (!top && side != Direction.DOWN) || (top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return adjDir == dir && top == (adjRot == Rotation.DOWN) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (!adjRot.isVertical() || side.getAxis() != adjRot.withFacing(adjDir).getAxis()) { return false; }

        if (top == (side == Direction.UP) && ((adjDir == dir && !adjFront) || (adjDir == dir.getOpposite() && adjFront)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        Rotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!adjRot.isVertical() || side.getAxis() != adjRot.withFacing(adjDir).getAxis()) { return false; }

        boolean adjUp = adjRot == Rotation.UP;
        if (!adjUp && ((!top && adjDir == dir.getOpposite()) || (top && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjUp && ((!top && adjDir == dir) || (top && adjDir == dir.getOpposite())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}