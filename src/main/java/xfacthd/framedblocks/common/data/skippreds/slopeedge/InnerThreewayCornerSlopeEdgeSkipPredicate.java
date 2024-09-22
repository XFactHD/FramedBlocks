package xfacthd.framedblocks.common.data.skippreds.slopeedge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.QuarterTriangleDir;
import xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import xfacthd.framedblocks.common.data.skippreds.pillar.ThreewayCornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.MasonryCornerSegmentSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.ExtendedInnerCornerSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.ExtendedInnerCornerSlopePanelWallSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.LargeCornerSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.LargeCornerSlopePanelWallSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.LargeInnerCornerSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.LargeInnerCornerSlopePanelWallSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.HalfStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.StairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalHalfStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalStairsSkipPredicate;

@CullTest(BlockType.FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE)
public final class InnerThreewayCornerSlopeEdgeSkipPredicate implements SideSkipPredicate
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
                case FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstInnerThreewayCornerSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstThreewayCornerSlopeEdge(
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
                case FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE -> testAgainstElevatedInnerCornerSlopeEdge(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL -> testAgainstLargeCornerSlopePanel(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> testAgainstLargeInnerCornerSlopePanel(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerCornerSlopePanelWall(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, top, right, alt, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_PILLAR -> testAgainstThreewayCornerPillar(
                        dir, top, right, alt, adjState, side
                );
                default -> false;
            };
        }
        return false;
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

        return getTriDir(dir, top, right, alt, side).isEqualTo(getTriDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite())) ||
               getStairDir(dir, top, right, alt, side).isEqualTo(getStairDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
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

        return getTriDir(dir, top, right, alt, side).isEqualTo(ThreewayCornerSlopeEdgeSkipPredicate.getTriDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
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

        return getTriDir(dir, top, right, alt, side).isEqualTo(InnerCornerSlopeEdgeSkipPredicate.getTriDir(adjDir, adjType, adjAlt, side.getOpposite())) ||
               getStairDir(dir, top, right, alt, side).isEqualTo(InnerCornerSlopeEdgeSkipPredicate.getStairDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedInnerCornerSlopeEdge(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getStairDir(dir, top, right, alt, side).isEqualTo(ElevatedInnerCornerSlopeEdgeSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return getStairDir(dir, top, right, alt, side).isEqualTo(StairsSkipPredicate.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getStairDir(dir, top, right, alt, side).isEqualTo(VerticalStairsSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getStairDir(dir, top, right, alt, side).isEqualTo(HalfStairsSkipPredicate.getStairDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, right, alt, side).isEqualTo(VerticalHalfStairsSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, right, alt, side).isEqualTo(LargeCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, top, right, alt, side).isEqualTo(LargeCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, right, alt, side).isEqualTo(LargeInnerCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, top, right, alt, side).isEqualTo(LargeInnerCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, right, alt, side).isEqualTo(ExtendedInnerCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, top, right, alt, side).isEqualTo(ExtendedInnerCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, right, alt, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_PILLAR)
    private static boolean testAgainstThreewayCornerPillar(
            Direction dir, boolean top, boolean right, boolean alt, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, right, alt, side).isEqualTo(ThreewayCornerPillarSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }



    public static QuarterTriangleDir getTriDir(Direction dir, boolean top, boolean right, boolean alt, Direction side)
    {
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dir.getOpposite())
        {
            return QuarterTriangleDir.fromDirections(bottom, dirTwo, alt);
        }
        if (side == dirTwo.getOpposite())
        {
            return QuarterTriangleDir.fromDirections(bottom, dir, alt);
        }
        if (side == bottom.getOpposite())
        {
            return QuarterTriangleDir.fromDirections(dir, dirTwo, alt);
        }
        return QuarterTriangleDir.NULL;
    }

    public static TriangleDir getStairDir(Direction dir, boolean top, boolean right, boolean alt, Direction side)
    {
        if (!alt)
        {
            Direction bottom = top ? Direction.UP : Direction.DOWN;
            Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();
            if (side == dir)
            {
                return TriangleDir.fromDirections(bottom, dirTwo);
            }
            if (side == dirTwo)
            {
                return TriangleDir.fromDirections(bottom, dir);
            }
            if (side == bottom)
            {
                return TriangleDir.fromDirections(dir, dirTwo);
            }
        }
        return TriangleDir.NULL;
    }
}
