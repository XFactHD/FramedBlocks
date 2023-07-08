package xfacthd.framedblocks.common.data.skippreds.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import xfacthd.framedblocks.common.data.skippreds.stairs.SlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalSlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class SlopeSkipPredicate implements SideSkipPredicate
{
    public static final SlopeSkipPredicate INSTANCE = new SlopeSkipPredicate();

    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side))
        {
            return true;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = FramedUtils.getSlopeBlockFacing(state);
            SlopeType type = FramedUtils.getSlopeType(state);

            return switch (blockType)
            {
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> testAgainstSlope(
                             dir, type, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
                        dir, type, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, type, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, type, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        dir, type, adjState, side
                );
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(
                        dir, type, adjState, side
                );
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(
                        dir, type, adjState, side
                );
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(
                        dir, type, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, type, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, type, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = FramedUtils.getSlopeBlockFacing(adjState);
        SlopeType adjType = FramedUtils.getSlopeType(adjState);

        return getTriDir(dir, type, side).isEqualTo(getTriDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlope(dir, type, states.getA(), side) ||
               testAgainstSlope(dir, type, states.getB(), side);
    }

    private static boolean testAgainstCorner(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, type, side).isEqualTo(CornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstInnerCorner(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, type, side).isEqualTo(InnerCornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstDoubleCorner(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerCorner(dir, type, states.getA(), side) ||
               testAgainstCorner(dir, type, states.getB(), side);
    }

    private static boolean testAgainstThreewayCorner(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(ThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(InnerThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleThreewayCorner(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerThreewayCorner(dir, type, states.getA(), side) ||
               testAgainstThreewayCorner(dir, type, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        if (type == SlopeType.HORIZONTAL)
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getTriDir(dir, type, side).isEqualTo(HalfSlopeSkipPredicate.getTriDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return testAgainstVerticalHalfSlope(dir, type, states.getA(), side) ||
                   testAgainstVerticalHalfSlope(dir, type, states.getB(), side);
        }
        else
        {
            return testAgainstHalfSlope(dir, type, states.getA(), side) ||
                   testAgainstHalfSlope(dir, type, states.getB(), side);
        }
    }

    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        if (type == SlopeType.HORIZONTAL)
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, type, states.getA(), side) ||
               testAgainstHalfSlope(dir, type, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        if (type != SlopeType.HORIZONTAL)
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(VerticalHalfSlopeSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        if (type != SlopeType.HORIZONTAL)
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfSlope(dir, type, states.getA(), side) ||
               testAgainstVerticalHalfSlope(dir, type, states.getB(), side);
    }

    private static boolean testAgainstSlopedStairs(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        if (type != SlopeType.HORIZONTAL || !Utils.isY(side))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(SlopedStairsSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, SlopeType type, BlockState adjState, Direction side
    )
    {
        if (type == SlopeType.HORIZONTAL)
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, type, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }



    public static TriangleDir getTriDir(Direction dir, SlopeType type, Direction side)
    {
        if (type == SlopeType.HORIZONTAL)
        {
            if (Utils.isY(side))
            {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
        }
        else
        {
            if (side.getAxis() == dir.getClockWise().getAxis())
            {
                return TriangleDir.fromDirections(
                        dir,
                        type == SlopeType.TOP ? Direction.UP : Direction.DOWN
                );
            }
        }
        return TriangleDir.NULL;
    }
}