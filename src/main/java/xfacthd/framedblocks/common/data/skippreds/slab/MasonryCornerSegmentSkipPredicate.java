package xfacthd.framedblocks.common.data.skippreds.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.slope.HalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
public final class MasonryCornerSegmentSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            return switch (type)
            {
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, adjState, side
                );
                case FRAMED_MASONRY_CORNER -> testAgainstMasonryCorner(
                        dir, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        dir, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstSmallDoubleCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstLargeDoubleCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstInverseDoubleCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedDoubleCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerDoubleCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> testAgainstStackedCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> testAgainstStackedInnerCornerSlopePanelWall(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        dir, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHorizontal(
                        dir, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        dir, adjState, side
                );
                case FRAMED_CHECKERED_SLAB_SEGMENT -> testAgainstCheckeredSlabSegment(
                        dir, adjState, side
                );
                case FRAMED_CHECKERED_SLAB -> testAgainstCheckeredSlab(
                        dir, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        dir, adjState, side
                );
                case FRAMED_CHECKERED_PANEL -> testAgainstCheckeredPanel(
                        dir, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        // Stair and corner faces will never occlude
        return getHalfDir(dir, side).isEqualTo(getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_MASONRY_CORNER,
            partTargets = BlockType.FRAMED_MASONRY_CORNER_SEGMENT
    )
    private static boolean testAgainstMasonryCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstMasonryCornerSegment(dir, blockPair.getA(), side) ||
               testAgainstMasonryCornerSegment(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())) ||
               getCornerDir(dir, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return getHalfDir(dir, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_PANEL,
            partTargets = BlockType.FRAMED_PANEL
    )
    private static boolean testAgainstDoublePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, blockPair.getA(), side) ||
               testAgainstPanel(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        // Stairs will never touch with corner faces
        return getStairDir(dir, side).isEqualTo(StairsSkipPredicate.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())) ||
               getHalfDir(dir, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getStairDir(dir, side).isEqualTo(VerticalStairsSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite())) ||
               getHalfDir(dir, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite())) ||
               getCornerDir(dir, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getStairDir(dir, side).isEqualTo(HalfStairsSkipPredicate.getStairDir(adjDir, adjTop, adjRight, side.getOpposite())) ||
               getHalfDir(dir, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())) ||
               getCornerDir(dir, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SLOPE_PANEL
    )
    private static boolean testAgainstDoubleSlopePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, blockPair.getA(), side) ||
               testAgainstSlopePanel(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SLOPE_PANEL
    )
    private static boolean testAgainstInverseDoubleSlopePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, blockPair.getA(), side) ||
               testAgainstSlopePanel(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_EXTENDED_SLOPE_PANEL, BlockType.FRAMED_SLOPE_PANEL }
    )
    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(dir, blockPair.getA(), side) ||
               testAgainstSlopePanel(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER,
            ignoredParts = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER
    )
    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, blockPair.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER,
            ignoredParts = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER
    )
    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, blockPair.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, blockPair.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, side).isEqualTo(LargeCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, side).isEqualTo(LargeInnerCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, side).isEqualTo(ExtendedCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, side).isEqualTo(ExtendedInnerCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, blockPair.getA(), side) ||
               testAgainstSmallCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstLargeDoubleCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanelWall(dir, blockPair.getA(), side) ||
               testAgainstLargeCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeCornerSlopePanelWall(dir, blockPair.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedCornerSlopePanelWall(dir, blockPair.getA(), side) ||
               testAgainstLargeInnerCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedInnerCornerSlopePanelWall(dir, blockPair.getA(), side) ||
               testAgainstSmallCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SLAB_EDGE, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstStackedCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, blockPair.getA(), side) ||
               testAgainstLargeCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_STAIRS }
    )
    private static boolean testAgainstStackedInnerCornerSlopePanelWall(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, blockPair.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanelWall(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_STAIRS,
            partTargets = { BlockType.FRAMED_STAIRS, BlockType.FRAMED_SLAB_EDGE }
    )
    private static boolean testAgainstDoubleStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, blockPair.getA(), side) ||
               testAgainstSlabEdge(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLOPE,
            partTargets = BlockType.FRAMED_HALF_SLOPE
    )
    private static boolean testAgainstDividedSlope(
            Direction dir, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) != SlopeType.HORIZONTAL)
        {
            Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
            return testAgainstHalfSlope(dir, blockPair.getA(), side) ||
                   testAgainstHalfSlope(dir, blockPair.getB(), side);
        }
        return false;
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_HALF_SLOPE,
            partTargets = BlockType.FRAMED_HALF_SLOPE
    )
    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, blockPair.getA(), side) ||
               testAgainstHalfSlope(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLAB,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedSlab(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, blockPair.getA(), side) ||
               testAgainstSlabEdge(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedPanelHorizontal(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, blockPair.getA(), side) ||
               testAgainstSlabEdge(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_PANEL, BlockType.FRAMED_SLOPE_PANEL }
    )
    private static boolean testAgainstStackedSlopePanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, blockPair.getA(), side) ||
               testAgainstSlopePanel(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER,
            partTargets = BlockType.FRAMED_PANEL,
            ignoredParts = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER
    )
    private static boolean testAgainstFlatStackedSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, blockPair.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_PANEL, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, blockPair.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_HALF_STAIRS
    )
    private static boolean testAgainstDividedStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, blockPair.getA(), side) ||
               testAgainstHalfStairs(dir, blockPair.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_STAIRS
    )
    private static boolean testAgainstVerticalDividedStairs(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, blockPair.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, blockPair.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT)
    private static boolean testAgainstCheckeredSlabSegment(
            Direction dir, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getCornerDir(dir, side).isEqualTo(CheckeredSlabSegmentSkipPredicate.getCornerDir(adjTop, adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_SLAB,
            partTargets = BlockType.FRAMED_CHECKERED_SLAB_SEGMENT
    )
    private static boolean testAgainstCheckeredSlab(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredSlabSegment(dir, states.getA(), side) ||
               testAgainstCheckeredSlabSegment(dir, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getCornerDir(dir, side).isEqualTo(CheckeredPanelSegmentSkipPredicate.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_PANEL,
            partTargets = BlockType.FRAMED_CHECKERED_PANEL_SEGMENT
    )
    private static boolean testAgainstCheckeredPanel(
            Direction dir, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredPanelSegment(dir, states.getA(), side) ||
               testAgainstCheckeredPanelSegment(dir, states.getB(), side);
    }



    public static HalfDir getHalfDir(Direction dir, Direction side)
    {
        return switch (side)
        {
            case DOWN -> HalfDir.fromDirections(side, dir.getOpposite());
            case UP -> HalfDir.fromDirections(side, dir.getClockWise());
            default -> HalfDir.NULL;
        };
    }

    public static CornerDir getCornerDir(Direction dir, Direction side)
    {
        if (side == dir)
        {
            return CornerDir.fromDirections(side, Direction.UP, dir.getClockWise());
        }
        if (side == dir.getCounterClockWise())
        {
            return CornerDir.fromDirections(side, Direction.DOWN, dir.getOpposite());
        }
        return CornerDir.NULL;
    }

    public static TriangleDir getStairDir(Direction dir, Direction side)
    {
        if (side == dir.getClockWise())
        {
            return TriangleDir.fromDirections(Direction.UP, dir.getOpposite());
        }
        if (side == dir.getOpposite())
        {
            return TriangleDir.fromDirections(Direction.DOWN, dir.getClockWise());
        }
        return TriangleDir.NULL;
    }
}
