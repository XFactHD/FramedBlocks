package xfacthd.framedblocks.common.data.skippreds.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.skippreds.CornerDir;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_SLAB_CORNER)
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
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(dir, top, adjState, side);
                case FRAMED_MASONRY_CORNER -> testAgainstMasonryCorner(dir, top, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(dir, top, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(dir, top, adjState, side);
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(dir, top, adjState, side);
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(dir, top, adjState, side);
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(dir, top, adjState, side);
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstSmallDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstSmallDoubleCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstInverseDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstInverseDoubleCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedDoubleCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerDoubleCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL -> testAgainstStackedCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> testAgainstStackedCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL -> testAgainstStackedInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> testAgainstStackedInnerCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstCorner(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstEdge(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLAB,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedSlab(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getA(), side) ||
               testAgainstEdge(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedPanelHor(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getA(), side) ||
               testAgainstEdge(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstPillar(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getCornerDir(dir, top, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_VERTICAL,
            partTargets = BlockType.FRAMED_CORNER_PILLAR
    )
    private static boolean testAgainstDividedPanelVert(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, top, states.getA(), side) ||
               testAgainstPillar(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getCornerDir(dir, top, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_MASONRY_CORNER,
            partTargets = BlockType.FRAMED_MASONRY_CORNER_SEGMENT
    )
    private static boolean testAgainstMasonryCorner(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstMasonryCornerSegment(dir, top, states.getA(), side) ||
               testAgainstMasonryCornerSegment(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_STAIRS)
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

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_STAIRS,
            partTargets = BlockType.FRAMED_SLAB_EDGE,
            ignoredParts = BlockType.FRAMED_STAIRS
    )
    private static boolean testAgainstDoubleStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getCornerDir(dir, top, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS,
            partTargets = BlockType.FRAMED_CORNER_PILLAR,
            ignoredParts = BlockType.FRAMED_VERTICAL_STAIRS
    )
    private static boolean testAgainstVerticalDoubleStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getCornerDir(dir, top, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_HALF_STAIRS
    )
    private static boolean testAgainstDividedStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, top, states.getA(), side) ||
               testAgainstHalfStairs(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_STAIRS
    )
    private static boolean testAgainstVerticalDividedStairs(Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, top, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(SmallCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, top, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(SmallInnerCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, top, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(ExtendedCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, top, side).isEqualTo(ExtendedCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, top, states.getA(), side) ||
               testAgainstSmallCornerSlopePanel(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, top, states.getA(), side) ||
               testAgainstSmallCornerSlopePanelWall(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedCornerSlopePanel(dir, top, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedCornerSlopePanelWall(dir, top, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanel(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanelWall(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_CORNER_PILLAR
    )
    private static boolean testAgainstStackedCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, top, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstStackedCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, top, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL,
            ignoredParts = BlockType.FRAMED_VERTICAL_STAIRS
    )
    private static boolean testAgainstStackedInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W,
            ignoredParts = BlockType.FRAMED_STAIRS
    )
    private static boolean testAgainstStackedInnerCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, top, states.getB(), side);
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