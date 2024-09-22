package xfacthd.framedblocks.common.data.skippreds.slopeedge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.QuarterTriangleDir;

@CullTest(BlockType.FRAMED_THREEWAY_CORNER_SLOPE_EDGE)
public final class ThreewayCornerSlopeEdgeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);
            boolean alt = state.getValue(PropertyHolder.ALT_TYPE);

            return switch (blockType)
            {
                case FRAMED_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstThreewayCornerSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstInnerThreewayCornerSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_CORNER_SLOPE_EDGE -> testAgainstCornerSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_SLOPE_EDGE)
    private static boolean testAgainstThreewayCornerSlopeEdge(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getTriDir(dir, top, right, alt, side).isEqualTo(getTriDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerThreewayCornerSlopeEdge(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getTriDir(dir, top, right, alt, side).isEqualTo(InnerThreewayCornerSlopeEdgeSkipPredicate.getTriDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getTriDir(dir, top, right, alt, side).isEqualTo(SlopeEdgeSkipPredicate.getTriDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstCornerSlopeEdge(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getTriDir(dir, top, right, alt, side).isEqualTo(CornerSlopeEdgeSkipPredicate.getTriDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getTriDir(dir, top, right, alt, side).isEqualTo(InnerCornerSlopeEdgeSkipPredicate.getTriDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }



    public static QuarterTriangleDir getTriDir(Direction dir, boolean top, boolean right, boolean alt, Direction side)
    {
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dir)
        {
            return QuarterTriangleDir.fromDirections(bottom, dirTwo, alt);
        }
        if (side == dirTwo)
        {
            return QuarterTriangleDir.fromDirections(bottom, dir, alt);
        }
        if (side == bottom)
        {
            return QuarterTriangleDir.fromDirections(dir, dirTwo, alt);
        }
        return QuarterTriangleDir.NULL;
    }
}
