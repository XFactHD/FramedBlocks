package xfacthd.framedblocks.common.data.skippreds.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.block.ISlopeBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import xfacthd.framedblocks.common.data.skippreds.stairs.SlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalSlopedStairsSkipPredicate;

@CullTest(BlockType.FRAMED_INNER_CORNER_SLOPE)
public final class InnerCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

            return switch (blockType)
            {
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, type, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, type, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        dir, type, adjState, side
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
                             dir, type, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
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

    @CullTest.SingleTarget(BlockType.FRAMED_INNER_CORNER_SLOPE)
    private static boolean testAgainstInnerCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, type, side).isEqualTo(getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CORNER_SLOPE)
    private static boolean testAgainstCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, type, side).isEqualTo(CornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_CORNER,
            partTargets = { BlockType.FRAMED_INNER_CORNER_SLOPE, BlockType.FRAMED_CORNER_SLOPE }
    )
    private static boolean testAgainstDoubleCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerCorner(dir, type, states.getA(), side) ||
               testAgainstCorner(dir, type, states.getB(), side);
    }

    @CullTest.SingleTarget({
            BlockType.FRAMED_SLOPE,
            BlockType.FRAMED_RAIL_SLOPE,
            BlockType.FRAMED_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE
    })
    @CullTest.DoubleTargets({
            @CullTest.DoubleTarget(
                    value = BlockType.FRAMED_FANCY_RAIL_SLOPE,
                    partTargets = BlockType.FRAMED_SLOPE
            ),
            @CullTest.DoubleTarget(
                    value = BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE,
                    partTargets = BlockType.FRAMED_SLOPE
            ),
            @CullTest.DoubleTarget(
                    value = BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                    partTargets = BlockType.FRAMED_SLOPE
            ),
            @CullTest.DoubleTarget(
                    value = BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE,
                    partTargets = BlockType.FRAMED_SLOPE
            )
    })
    private static boolean testAgainstSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        ISlopeBlock block = (ISlopeBlock) adjState.getBlock();
        Direction adjDir = block.getFacing(adjState);
        SlopeType adjType = block.getSlopeType(adjState);

        return getTriDir(dir, type, side).isEqualTo(SlopeSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLOPE,
            partTargets = BlockType.FRAMED_SLOPE
    )
    private static boolean testAgainstDoubleSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlope(dir, type, states.getA(), side) ||
               testAgainstSlope(dir, type, states.getB(), side);
    }

    @CullTest.SingleTarget({ BlockType.FRAMED_THREEWAY_CORNER, BlockType.FRAMED_PRISM_CORNER })
    private static boolean testAgainstThreewayCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(ThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget({ BlockType.FRAMED_INNER_THREEWAY_CORNER, BlockType.FRAMED_INNER_PRISM_CORNER })
    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(InnerThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTargets({
            @CullTest.DoubleTarget(
                    value = BlockType.FRAMED_DOUBLE_THREEWAY_CORNER,
                    partTargets = { BlockType.FRAMED_INNER_THREEWAY_CORNER, BlockType.FRAMED_THREEWAY_CORNER }
            ),
            @CullTest.DoubleTarget(
                    value = BlockType.FRAMED_DOUBLE_PRISM_CORNER,
                    partTargets = { BlockType.FRAMED_INNER_PRISM_CORNER, BlockType.FRAMED_PRISM_CORNER }
            )
    })
    private static boolean testAgainstDoubleThreewayCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerThreewayCorner(dir, type, states.getA(), side) ||
               testAgainstThreewayCorner(dir, type, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getTriDir(dir, type, side).isEqualTo(HalfSlopeSkipPredicate.getTriDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLOPE,
            partTargets = { BlockType.FRAMED_VERTICAL_HALF_SLOPE, BlockType.FRAMED_HALF_SLOPE }
    )
    private static boolean testAgainstDividedSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
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

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_HALF_SLOPE,
            partTargets = BlockType.FRAMED_HALF_SLOPE
    )
    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, type, states.getA(), side) ||
               testAgainstHalfSlope(dir, type, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(VerticalHalfSlopeSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_SLOPE
    )
    private static boolean testAgainstVerticalDoubleHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfSlope(dir, type, states.getA(), side) ||
               testAgainstVerticalHalfSlope(dir, type, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        boolean top = type.isTop();
        if (!type.isHorizontal() || (!top && side != Direction.UP) || (top && side != Direction.DOWN))
        {
            return false;
        }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, type, side).isEqualTo(SlopedStairsSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, type, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }



    public static TriangleDir getTriDir(Direction dir, CornerType type, Direction side)
    {
        if (type.isHorizontal())
        {
            boolean top = type.isTop();
            boolean right = type.isRight();

            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
            {
                return TriangleDir.fromDirections(
                        dir,
                        right ? dir.getClockWise() : dir.getCounterClockWise()
                );
            }
            else if ((!right && side == dir.getClockWise()) || (right && side == dir.getCounterClockWise()))
            {
                return TriangleDir.fromDirections(
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
        }
        else if (side == dir.getOpposite())
        {
            return TriangleDir.fromDirections(
                    dir.getCounterClockWise(),
                    type == CornerType.TOP ? Direction.UP : Direction.DOWN
            );
        }
        else if (side == dir.getClockWise())
        {
            return TriangleDir.fromDirections(
                    dir,
                    type == CornerType.TOP ? Direction.UP : Direction.DOWN
            );
        }
        return TriangleDir.NULL;
    }
}