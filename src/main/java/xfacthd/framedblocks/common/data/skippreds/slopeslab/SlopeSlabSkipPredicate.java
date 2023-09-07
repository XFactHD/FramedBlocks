package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_SLOPE_SLAB)
public final class SlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

            return switch (type)
            {
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_STACKED_SLOPE_SLAB -> testAgainstStackedSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, topHalf, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER -> testAgainstFlatStackedSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatStackedInnerSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstSmallDoubleCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstLargeDoubleCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstInverseDoubleCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedDoubleCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerDoubleCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> testAgainstStackedCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> testAgainstStackedInnerCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, topHalf, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        dir, topHalf, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, topHalf, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS, FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfStairs(
                        dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        dir, topHalf, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        dir, topHalf, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, topHalf, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getTriDir(dir, topHalf, top, side).isEqualTo(getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, topHalf, side).isEqualTo(getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLOPE_SLAB,
            partTargets = BlockType.FRAMED_SLOPE_SLAB
    )
    private static boolean testAgainstDoubleSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, top, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB,
            partTargets = BlockType.FRAMED_SLOPE_SLAB
    )
    private static boolean testAgainstInverseDoubleSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, top, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB,
            partTargets = { BlockType.FRAMED_ELEVATED_SLOPE_SLAB, BlockType.FRAMED_SLOPE_SLAB }
    )
    private static boolean testAgainstElevatedDoubleSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(dir, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_SLOPE_SLAB,
            partTargets = { BlockType.FRAMED_SLAB, BlockType.FRAMED_SLOPE_SLAB }
    )
    private static boolean testAgainstStackedSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, topHalf, states.getA(), side) ||
               testAgainstSlopeSlab(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getTriDir(dir, topHalf, top, side).isEqualTo(FlatSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getHalfDir(dir, topHalf, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getTriDir(dir, topHalf, top, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER }
    )
    private static boolean testAgainstFlatDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER }
    )
    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER,
            partTargets = { BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER }
    )
    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(dir, topHalf, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER,
            partTargets = BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER
    )
    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER,
            partTargets = { BlockType.FRAMED_SLAB, BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER }
    )
    private static boolean testAgainstFlatStackedSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, topHalf, states.getA(), side) ||
               testAgainstFlatSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER,
            partTargets = { BlockType.FRAMED_SLAB, BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER }
    )
    private static boolean testAgainstFlatStackedInnerSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, topHalf, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(LargeCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(LargeInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstSmallDoubleCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallInnerCornerSlopePanelWall(dir, top, topHalf, states.getA(), side) ||
               testAgainstSmallCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstLargeDoubleCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanelWall(dir, top, topHalf, states.getA(), side) ||
               testAgainstLargeCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstInverseDoubleCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeCornerSlopePanelWall(dir, top, topHalf, states.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstLargeInnerCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSmallCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_SLAB_EDGE, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstStackedCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, topHalf, states.getA(), side) ||
               testAgainstLargeCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W,
            partTargets = { BlockType.FRAMED_STAIRS, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W }
    )
    private static boolean testAgainstStackedInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, topHalf, states.getA(), side) ||
               testAgainstSmallInnerCornerSlopePanelWall(dir, top, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getHalfDir(dir, topHalf, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLAB,
            partTargets = BlockType.FRAMED_SLAB
    )
    private static boolean testAgainstDoubleSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, topHalf, states.getA(), side) ||
               testAgainstSlab(dir, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLAB,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, topHalf, states.getA(), side) ||
               testAgainstSlabEdge(dir, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL,
            partTargets = BlockType.FRAMED_SLAB_EDGE
    )
    private static boolean testAgainstDividedPanelHor(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, topHalf, states.getA(), side) ||
               testAgainstSlabEdge(dir, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return getHalfDir(dir, topHalf, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_STAIRS,
            partTargets = { BlockType.FRAMED_STAIRS, BlockType.FRAMED_SLAB_EDGE }
    )
    private static boolean testAgainstDoubleStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, topHalf, states.getA(), side) ||
               testAgainstSlabEdge(dir, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget({ BlockType.FRAMED_VERTICAL_HALF_STAIRS, BlockType.FRAMED_VERTICAL_HALF_SLOPE })
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_STAIRS
    )
    private static boolean testAgainstVerticalDividedStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, topHalf, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DIVIDED_SLOPE,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_SLOPE
    )
    private static boolean testAgainstDividedSlope(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) != SlopeType.HORIZONTAL)
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(dir, topHalf, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, topHalf, states.getB(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE,
            partTargets = BlockType.FRAMED_VERTICAL_HALF_SLOPE
    )
    private static boolean testAgainstVerticalDoubleHalfSlope(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstVerticalHalfStairs(dir, topHalf, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, topHalf, states.getB(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side)
    {
        if (side.getAxis() == dir.getClockWise().getAxis())
        {
            Direction longEdge = top ? Direction.UP : Direction.DOWN;
            return HalfTriangleDir.fromDirections(longEdge, dir, topHalf == top);
        }
        return HalfTriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, boolean topHalf, Direction side)
    {
        if (side == dir)
        {
            return HalfDir.fromDirections(
                    side,
                    topHalf ? Direction.UP : Direction.DOWN
            );
        }
        return HalfDir.NULL;
    }
}
