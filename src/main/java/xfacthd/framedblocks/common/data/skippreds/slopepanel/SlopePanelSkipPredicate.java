package xfacthd.framedblocks.common.data.skippreds.slopepanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.PanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_SLOPE_PANEL)
public final class SlopePanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            boolean front = state.getValue(PropertyHolder.FRONT);

            return switch (type)
            {
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL -> testAgainstLargeCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> testAgainstLargeInnerCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstSmallDoubleCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstSmallDoubleCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstLargeDoubleCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstLargeDoubleCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstInverseDoubleCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstInverseDoubleCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedDoubleCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedDoubleCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerDoubleCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerDoubleCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL -> testAgainstStackedCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> testAgainstStackedCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL -> testAgainstStackedInnerCornerSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> testAgainstStackedInnerCornerSlopePanelWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        dir, rot, front, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_HALF_STAIRS, FRAMED_HALF_SLOPE -> testAgainstHalfStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, rot, front, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, rot, front, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, rot, front, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, rot, front, side).isEqualTo(getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return true;
        }
        else if (getTriDir(dir, rot, front, side).isEqualTo(getTriDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return true;
        }

        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, rot, front, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SLOPE_PANEL
    )
    private static boolean testAgainstDoubleSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SLOPE_PANEL
    )
    private static boolean testAgainstInverseDoubleSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_EXTENDED_SLOPE_PANEL, BlockType.FRAMED_SLOPE_PANEL }
    )
    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_PANEL, BlockType.FRAMED_SLOPE_PANEL }
    )
    private static boolean testAgainstStackedSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, rot, front, side).isEqualTo(FlatSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, rot, front, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return true;
        }
        else if (getTriDir(dir, rot, front, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, rot, front, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, rot, front, states.getA(), side) ||
               testAgainstFlatSlopePanelCorner(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, rot, front, states.getA(), side) ||
               testAgainstFlatSlopePanelCorner(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, rot, front, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER,
            partTargets = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER
    )
    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopePanelCorner(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_PANEL, BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatStackedSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, rot, front, states.getA(), side) ||
               testAgainstFlatSlopePanelCorner(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER,
            partTargets = { BlockType.FRAMED_PANEL, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER }
    )
    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, rot, front, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, rot, front, side).isEqualTo(SmallCornerSlopePanelSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, rot, front, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, rot, front, side).isEqualTo(LargeCornerSlopePanelSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, rot, front, side).isEqualTo(LargeCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, rot, front, side).isEqualTo(SmallInnerCornerSlopePanelSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, rot, front, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, rot, front, side).isEqualTo(LargeInnerCornerSlopePanelSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, rot, front, side).isEqualTo(LargeInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanel(dir, rot, front, states.getA(), side) ||
               testAgainstSmallCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, rot, front, states.getA(), side) ||
               testAgainstSmallCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstLargeDoubleCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanel(dir, rot, front, states.getA(), side) ||
               testAgainstLargeCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstLargeDoubleCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanelWall(dir, rot, front, states.getA(), side) ||
               testAgainstLargeCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeCornerSlopePanel(dir, rot, front, states.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeCornerSlopePanelWall(dir, rot, front, states.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_CORNER_PILLAR, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstStackedCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, rot, front, states.getA(), side) ||
               testAgainstLargeCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SLAB_EDGE, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstStackedCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, rot, front, states.getA(), side) ||
               testAgainstLargeCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL,
            partTargets = { BlockType.FRAMED_VERTICAL_STAIRS, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL }
    )
    private static boolean testAgainstStackedInnerCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(dir, rot, front, states.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_STAIRS, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstStackedInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, rot, front, states.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanelWall(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, rot, front, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLAB,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedSlab(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, rot, front, states.getA(), side) ||
               testAgainstSlabEdge(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedPanelHor(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, rot, front, states.getA(), side) ||
               testAgainstSlabEdge(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, rot, front, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_PANEL,
            partTargets = BlockType.FRAMED_PANEL
    )
    private static boolean testAgainstDoublePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, rot, front, states.getA(), side) ||
               testAgainstPanel(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, rot, front, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_VERTICAL,
            partTargets = BlockType.FRAMED_CORNER_PILLAR
    )
    private static boolean testAgainstDividedPanelVert(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, rot, front, states.getA(), side) ||
               testAgainstCornerPillar(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        return getHalfDir(dir, rot, front, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_STAIRS,
            partTargets = { BlockType.FRAMED_STAIRS, BlockType.FRAMED_SLAB_EDGE }
    )
    private static boolean testAgainstDoubleStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, rot, front, states.getA(), side) ||
               testAgainstSlabEdge(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getHalfDir(dir, rot, front, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS,
            partTargets = { BlockType.FRAMED_VERTICAL_STAIRS, BlockType.FRAMED_CORNER_PILLAR }
    )
    private static boolean testAgainstVerticalDoubleStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(dir, rot, front, states.getA(), side) ||
               testAgainstCornerPillar(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget({ BlockType.FRAMED_HALF_STAIRS, BlockType.FRAMED_HALF_SLOPE })
    private static boolean testAgainstHalfStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, rot, front, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_HALF_STAIRS
    )
    private static boolean testAgainstDividedStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, rot, front, states.getA(), side) ||
               testAgainstHalfStairs(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLOPE,
            partTargets = BlockType.FRAMED_HALF_SLOPE
    )
    private static boolean testAgainstDividedSlope(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstHalfStairs(dir, rot, front, states.getA(), side) ||
               testAgainstHalfStairs(dir, rot, front, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_HALF_SLOPE,
            partTargets = BlockType.FRAMED_HALF_SLOPE
    )
    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstHalfStairs(dir, rot, front, states.getA(), side) ||
               testAgainstHalfStairs(dir, rot, front, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, rot, front, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }



    public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, boolean front, Direction side)
    {
        Direction perpRotDir = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir);
        if (side.getAxis() == perpRotDir.getAxis())
        {
            Direction shortEdge = rot.getOpposite().withFacing(dir);
            return HalfTriangleDir.fromDirections(dir, shortEdge, !front);
        }
        return HalfTriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, boolean front, Direction side)
    {
        if (side == rot.withFacing(dir).getOpposite())
        {
            return HalfDir.fromDirections(
                    side,
                    front ? dir.getOpposite() : dir
            );
        }
        return HalfDir.NULL;
    }
}
