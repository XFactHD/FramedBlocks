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
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
public final class CheckeredPanelSegmentSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean second = state.getValue(PropertyHolder.SECOND);
            return switch (type)
            {
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        dir, second, adjState, side
                );
                case FRAMED_CHECKERED_PANEL -> testAgainstCheckeredPanel(
                        dir, second, adjState, side
                );
                case FRAMED_CHECKERED_CUBE_SEGMENT -> testAgainstCheckeredCubeSegment(
                        dir, second, adjState, side
                );
                case FRAMED_CHECKERED_CUBE -> testAgainstCheckeredCube(
                        dir, second, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, second, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, second, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        dir, second, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHorizontal(
                        dir, second, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, second, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVertical(
                        dir, second, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, second, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        dir, second, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, second, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        dir, second, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, second, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, second, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, second, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        dir, second, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstSmallDoubleCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstSmallDoubleCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstInverseDoubleCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstInverseDoubleCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedDoubleCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedDoubleCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerDoubleCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerDoubleCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL -> testAgainstStackedCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> testAgainstStackedCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL -> testAgainstStackedInnerCornerSlopePanel(
                        dir, second, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> testAgainstStackedInnerCornerSlopePanelWall(
                        dir, second, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, second, adjState, side
                );
                case FRAMED_MASONRY_CORNER -> testAgainstMasonryCorner(
                        dir, second, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getDiagCornerDir(dir, second, side).isEqualTo(getDiagCornerDir(adjDir, adjSecond, side.getOpposite())) ||
               getCornerDir(dir, second, side).isEqualTo(getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_PANEL,
            partTargets = BlockType.FRAMED_CHECKERED_PANEL_SEGMENT
    )
    private static boolean testAgainstCheckeredPanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredPanelSegment(dir, second, states.getA(), side) ||
               testAgainstCheckeredPanelSegment(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_CUBE_SEGMENT)
    private static boolean testAgainstCheckeredCubeSegment(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getDiagCornerDir(dir, second, side).isEqualTo(CheckeredCubeSegmentSkipPredicate.getDiagCornerDir(adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_CUBE,
            partTargets = BlockType.FRAMED_CHECKERED_CUBE_SEGMENT
    )
    private static boolean testAgainstCheckeredCube(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredCubeSegment(dir, second, states.getA(), side) ||
               testAgainstCheckeredCubeSegment(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, second, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, second, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLAB,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedSlab(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, second, states.getA(), side) ||
               testAgainstSlabEdge(dir, second, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedPanelHorizontal(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, second, states.getA(), side) ||
               testAgainstSlabEdge(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getCornerDir(dir, second, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_VERTICAL,
            partTargets = BlockType.FRAMED_CORNER_PILLAR
    )
    private static boolean testAgainstDividedPanelVertical(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, second, states.getA(), side) ||
               testAgainstCornerPillar(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);
        return getCornerDir(dir, second, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_STAIRS,
            partTargets = BlockType.FRAMED_SLAB_EDGE,
            ignoredParts = BlockType.FRAMED_STAIRS
    )
    private static boolean testAgainstDoubleStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        return getCornerDir(dir, second, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS,
            partTargets = BlockType.FRAMED_CORNER_PILLAR,
            ignoredParts = BlockType.FRAMED_VERTICAL_STAIRS
    )
    private static boolean testAgainstVerticalDoubleStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        return getCornerDir(dir, second, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_HALF_STAIRS
    )
    private static boolean testAgainstDividedStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, second, states.getA(), side) ||
               testAgainstHalfStairs(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, second, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_STAIRS
    )
    private static boolean testAgainstVerticalDividedStairs(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, second, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, second, side).isEqualTo(SmallCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        return getCornerDir(dir, second, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, second, side).isEqualTo(SmallInnerCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        return getCornerDir(dir, second, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getCornerDir(dir, second, side).isEqualTo(ExtendedCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        return getCornerDir(dir, second, side).isEqualTo(ExtendedCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, second, states.getA(), side) ||
               testAgainstSmallCornerSlopePanel(dir, second, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, second, states.getA(), side) ||
               testAgainstSmallCornerSlopePanelWall(dir, second, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL,
            ignoredParts = BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, second, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W,
            ignoredParts = BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, second, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL,
            ignoredParts = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedCornerSlopePanel(dir, second, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W,
            ignoredParts = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedCornerSlopePanelWall(dir, second, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL,
            ignoredParts = BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanel(dir, second, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W,
            ignoredParts = BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanelWall(dir, second, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_CORNER_PILLAR,
            ignoredParts = BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstStackedCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, second, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SLAB_EDGE,
            ignoredParts = BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstStackedCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, second, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL,
            ignoredParts = BlockType.FRAMED_VERTICAL_STAIRS
    )
    private static boolean testAgainstStackedInnerCornerSlopePanel(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, second, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W,
            ignoredParts = BlockType.FRAMED_STAIRS
    )
    private static boolean testAgainstStackedInnerCornerSlopePanelWall(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, second, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getCornerDir(dir, second, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_MASONRY_CORNER,
            partTargets = BlockType.FRAMED_MASONRY_CORNER_SEGMENT
    )
    private static boolean testAgainstMasonryCorner(
            Direction dir, boolean second, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstMasonryCornerSegment(dir, second, states.getA(), side) ||
               testAgainstMasonryCornerSegment(dir, second, states.getB(), side);
    }



    public static DiagCornerDir getDiagCornerDir(Direction dir, boolean second, Direction side)
    {
        if (side == dir)
        {
            return switch (side)
            {
                case NORTH -> second ? DiagCornerDir.NORTH_UW_DE : DiagCornerDir.NORTH_UE_DW;
                case SOUTH -> second ? DiagCornerDir.SOUTH_UE_DW : DiagCornerDir.SOUTH_UW_DE;
                case WEST -> second ? DiagCornerDir.WEST_UN_DS : DiagCornerDir.WEST_US_DN;
                case EAST -> second ? DiagCornerDir.EAST_US_DN : DiagCornerDir.EAST_UN_DS;
                default -> DiagCornerDir.NULL;
            };
        }
        return DiagCornerDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, boolean second, Direction side)
    {
        if (Utils.isY(side))
        {
            boolean x = Utils.isX(dir);
            boolean up = side == Direction.UP;
            return CornerDir.fromDirections(
                    side,
                    dir,
                    (second == up) ^ x ? dir.getCounterClockWise() : dir.getClockWise()
            );
        }
        else if (side.getAxis() != dir.getAxis())
        {
            boolean x = Utils.isX(dir);
            boolean cw = side == dir.getClockWise();
            return CornerDir.fromDirections(
                    side,
                    dir,
                    (second == cw) ^ x ? Direction.DOWN : Direction.UP
            );
        }
        return CornerDir.NULL;
    }
}
