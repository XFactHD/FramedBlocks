package xfacthd.framedblocks.common.data.skippreds.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import xfacthd.framedblocks.common.data.skippreds.stairs.SlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalSlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class InnerThreewayCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side))
        {
            return true;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (type)
            {
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(
                        dir, top, adjState, side
                );
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(
                        dir, top, adjState, side
                );
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> testAgainstSlope(
                             dir, top, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
                        dir, top, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, top, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, top, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        dir, top, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, top, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, top, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        dir, top, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstThreewayCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(ThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleThreewayCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerThreewayCorner(dir, top, states.getA(), side) ||
               testAgainstThreewayCorner(dir, top, states.getB(), side);
    }

    private static boolean testAgainstSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = FramedUtils.getSlopeBlockFacing(adjState);
        SlopeType adjType = FramedUtils.getSlopeType(adjState);

        return getTriDir(dir, top, side).isEqualTo(SlopeSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlope(dir, top, states.getA(), side) ||
               testAgainstSlope(dir, top, states.getB(), side);
    }

    private static boolean testAgainstCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, top, side).isEqualTo(CornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstInnerCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, top, side).isEqualTo(InnerCornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    private static boolean testAgainstDoubleCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerCorner(dir, top, states.getA(), side) ||
               testAgainstCorner(dir, top, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getTriDir(dir, top, side).isEqualTo(HalfSlopeSkipPredicate.getTriDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return testAgainstVerticalHalfSlope(dir, top, states.getA(), side) ||
                   testAgainstVerticalHalfSlope(dir, top, states.getB(), side);
        }
        else
        {
            return testAgainstHalfSlope(dir, top, states.getA(), side) ||
                   testAgainstHalfSlope(dir, top, states.getB(), side);
        }
    }

    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, top, states.getA(), side) ||
               testAgainstHalfSlope(dir, top, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(VerticalHalfSlopeSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP))
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfSlope(dir, top, states.getA(), side) ||
               testAgainstVerticalHalfSlope(dir, top, states.getB(), side);
    }

    private static boolean testAgainstSlopedStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(SlopedStairsSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        if (Utils.isY(side) || (side != dir.getOpposite() && side != dir.getClockWise()))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }



    public static TriangleDir getTriDir(Direction dir, boolean top, Direction side)
    {
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
        }
        else if (side == dir.getOpposite())
        {
            return TriangleDir.fromDirections(
                    dir.getCounterClockWise(),
                    top ? Direction.UP : Direction.DOWN
            );
        }
        else if (side == dir.getClockWise())
        {
            return TriangleDir.fromDirections(
                    dir,
                    top ? Direction.UP : Direction.DOWN
            );
        }
        return TriangleDir.NULL;
    }
}