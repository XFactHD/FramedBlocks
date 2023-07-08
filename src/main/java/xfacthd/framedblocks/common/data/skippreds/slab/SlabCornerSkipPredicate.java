package xfacthd.framedblocks.common.data.skippreds.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.skippreds.CornerDir;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

public final class SlabCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_SLAB_CORNER -> testAgainstCorner(dir, top, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstEdge(dir, top, adjState, side);
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(dir, top, adjState, side);
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(dir, top, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(dir, top, adjState, side);
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(dir, top, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(dir, top, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(dir, top, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(dir, top, adjState, side);
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(dir, top, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstCorner(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstEdge(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlab(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getA(), side) ||
               testAgainstEdge(dir, top, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getA(), side) ||
               testAgainstEdge(dir, top, states.getB(), side);
    }

    private static boolean testAgainstPillar(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getCornerDir(dir, top, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    private static boolean testAgainstDividedPanelVert(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, top, states.getA(), side) ||
               testAgainstPillar(dir, top, states.getB(), side);
    }

    private static boolean testAgainstStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return getCornerDir(dir, top, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    private static boolean testAgainstDoubleStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getCornerDir(dir, top, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstVerticalDoubleStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, top, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getCornerDir(dir, top, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstDividedStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, top, states.getA(), side) ||
               testAgainstHalfStairs(dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalDividedStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, top, states.getB(), side);
    }



    public static CornerDir getCornerDir(Direction dir, boolean top, Direction side)
    {
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
        }
        else if (side == dir)
        {
            return CornerDir.fromDirections(
                    side,
                    dir.getCounterClockWise(),
                    top ? Direction.UP : Direction.DOWN
            );
        }
        else if (side == dir.getCounterClockWise())
        {
            return CornerDir.fromDirections(
                    side,
                    dir,
                    top ? Direction.UP : Direction.DOWN
            );
        }
        return CornerDir.NULL;
    }
}