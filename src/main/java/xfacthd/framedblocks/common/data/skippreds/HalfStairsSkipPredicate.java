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

public class HalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            Direction stairFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            Direction baseFace = top ? Direction.UP : Direction.DOWN;

            if (type == BlockType.FRAMED_HALF_STAIRS)
            {
                return testAgainstHalfStairs(level, pos, dir, top, right, stairFace, adjState, side);
            }

            if (side == stairFace)
            {
                return switch (type)
                {
                    case FRAMED_STAIRS -> testAgainstStairs(level, pos, top, adjState, side);
                    case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, dir, top, right, adjState, side);
                    default -> false;
                };
            }
            else
            {
                return switch (type)
                {
                    case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, dir, top, right, baseFace, adjState, side);
                    case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(level, pos, dir, right, baseFace, adjState, side);
                    case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(level, pos, dir, top, right, baseFace, adjState, side);
                    case FRAMED_PANEL -> testAgainstPanel(level, pos, dir, right, baseFace, adjState, side);
                    case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, dir, stairFace, baseFace, adjState, side);
                    case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, top, right, adjState, side);
                    default -> false;
                };
            }
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, Direction stairFace, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side == stairFace)
        {
            return adjDir == dir && adjTop == top && adjRight != right && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (Utils.isY(side))
        {
            return adjDir == dir && adjTop != top && adjRight == right && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == dir)
        {
            return adjDir == dir.getOpposite() && adjRight != right && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        return top == adjTop && StairsSkipPredicate.isStairSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstVerticalStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if ((right && adjDir == dir) || (!right && adjDir == dir.getClockWise()))
        {
            return adjType.isTop() != top && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return (adjTop == top) == (side == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstCornerPillar(BlockGetter level, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side == dir && ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (side == baseFace.getOpposite() && ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstPanel(BlockGetter level, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(BlockGetter level, BlockPos pos, Direction dir, Direction stairFace, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.getValue(PropertyHolder.FACING_NE);

        return adjDir.getAxis() != dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, stairFace);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (side != dir.getOpposite() || adjTop != top) { return false; }

        if ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }
}
