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
                case FRAMED_SLAB_CORNER -> testAgainstCorner(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstEdge(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        level, pos, state, dir, top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getCornerDir(dir, top, side).isEqualTo(getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getCornerDir(dir, top, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstEdge(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstEdge(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstPillar(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getCornerDir(dir, top, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstPillar(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (!Utils.isY(side))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        if (getCornerDir(dir, top, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getCornerDir(dir, top, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getCornerDir(dir, top, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstHalfStairs(level, pos, state, dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getCornerDir(dir, top, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDividedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(level, pos, state, dir, top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(level, pos, state, dir, top, states.getB(), side);
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