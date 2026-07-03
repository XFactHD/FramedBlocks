package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.block.SlopeBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.slope.SlopeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeslab.SlopeSlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.stairs.StairsDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/// This class is machine-generated, any manual changes to this class will be overwritten.
@CullTest(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W)
public final class LargeInnerPrismCornerSlopePanelWallSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);

            return switch (blockType) {
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerPrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_SLOPE -> testAgainstSlope(
                        dir, rot, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, rot, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_THREEWAY_CORNER,
                     FRAMED_PRISM_CORNER -> testAgainstThreewayCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER,
                     FRAMED_INNER_PRISM_CORNER -> testAgainstInnerThreewayCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, rot, adjState, side
                );
                case FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE -> testAgainstRailSlope(
                        dir, rot, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, rot, adjState, side
                );
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, rot, adjState, side
                );
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstSmallPrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargePrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerPrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerPrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(adjDir, adjRot, side.getOpposite())) ||
               SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getBackTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getBackTriDir(adjDir, adjRot, side.getOpposite())) ||
               SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE)
    private static boolean testAgainstSlope(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.Slope.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.HalfSlope.getTriDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE)
    private static boolean testAgainstCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.Corner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE)
    private static boolean testAgainstInnerCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.InnerCorner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_THREEWAY_CORNER,
            BlockType.FRAMED_PRISM_CORNER
    })
    private static boolean testAgainstThreewayCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.ThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_INNER_THREEWAY_CORNER,
            BlockType.FRAMED_INNER_PRISM_CORNER
    })
    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.InnerThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_RAIL_SLOPE,
            BlockType.FRAMED_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE
    })
    private static boolean testAgainstRailSlope(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = ((SlopeBlock) adjState.getBlock()).getFacing(adjState);
        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopeDirs.RailSlope.getTriDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopeSlabDirs.SlopeSlab.getTriDir(adjDir, adjTop, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatSlopeSlabCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopeSlabDirs.FlatSlopeSlabCorner.getTriDir(adjDir, adjTop, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopeSlabDirs.FlatInnerSlopeSlabCorner.getTriDir(adjDir, adjTop, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.SlopePanel.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.FlatSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallPrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallPrismCornerSlopePanelWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargePrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getFrontTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargePrismCornerSlopePanelWall.getBackTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerPrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getBackTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallInnerPrismCornerSlopePanelWall.getFrontTriDir(adjDir, adjRot, side.getOpposite()));
    }
}
