package xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

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
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.CheckeredPanelSegmentSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
public final class ExtendedCornerSlopePanelSkipPredicate implements SideSkipPredicate
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
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstSmallDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstInverseDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerDoubleCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL -> testAgainstStackedCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL -> testAgainstStackedInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, top, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, top, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVertical(
                        dir, top, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, top, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        dir, top, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, top, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        dir, top, adjState, side
                );
                case FRAMED_CHECKERED_PANEL -> testAgainstCheckeredPanel(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, side).isEqualTo(getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        if (getCornerDir(dir, top, side).isEqualTo(getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
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

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(SmallInnerCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(ExtendedInnerCornerSlopePanelSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
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
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedInnerCornerSlopePanel(dir, top, states.getA(), side) ||
               testAgainstSmallCornerSlopePanel(dir, top, states.getB(), side);
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
        return testAgainstCornerPillar(dir, top, states.getA(), side);
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

    @CullTest.SingleTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_EXTENDED_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(dir, top, states.getA(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(FlatExtendedInnerSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER
    )
    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, top, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER
    )
    private static boolean testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedInnerSlopePanelCorner(dir, top, states.getA(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        return getCornerDir(dir, top, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_VERTICAL,
            partTargets = BlockType.FRAMED_CORNER_PILLAR
    )
    private static boolean testAgainstDividedPanelVertical(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, top, states.getA(), side) ||
               testAgainstCornerPillar(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        return getCornerDir(dir, top, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getCornerDir(dir, top, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getCornerDir(dir, top, side).isEqualTo(
                HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())
        );
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS,
            partTargets = BlockType.FRAMED_CORNER_PILLAR,
            ignoredParts = BlockType.FRAMED_VERTICAL_STAIRS
    )
    private static boolean testAgainstVerticalDoubleStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, top, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_HALF_STAIRS
    )
    private static boolean testAgainstDividedStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, top, states.getA(), side) ||
               testAgainstHalfStairs(dir, top, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getCornerDir(dir, top, side).isEqualTo(CheckeredPanelSegmentSkipPredicate.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_CHECKERED_PANEL,
            partTargets = BlockType.FRAMED_CHECKERED_PANEL_SEGMENT
    )
    private static boolean testAgainstCheckeredPanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCheckeredPanelSegment(dir, top, states.getA(), side) ||
               testAgainstCheckeredPanelSegment(dir, top, states.getB(), side);
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side)
    {
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dir)
        {
            return HalfTriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo, false);
        }
        else if (side == dir.getCounterClockWise())
        {
            return HalfTriangleDir.fromDirections(dir, dirTwo, false);
        }
        return HalfTriangleDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, boolean top, Direction side)
    {
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
        }
        return CornerDir.NULL;
    }
}
