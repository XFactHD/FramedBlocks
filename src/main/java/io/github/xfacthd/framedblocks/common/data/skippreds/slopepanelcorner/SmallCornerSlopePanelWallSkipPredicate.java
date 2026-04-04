package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.pane.PaneDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.pillar.PillarDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slab.SlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge.SlopeEdgeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeslab.SlopeSlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.stairs.StairsDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
public final class SmallCornerSlopePanelWallSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);

            return switch (blockType) {
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_CORNER_SLOPE_EDGE -> testAgainstCornerSlopeEdge(
                        dir, rot, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, rot, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, rot, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, rot, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, rot, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, rot, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_PILLAR -> testAgainstThreewayCornerPillar(
                        dir, rot, adjState, side
                );
                case FRAMED_CORNER_BOARD -> testAgainstCornerBoard(
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
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstSmallPrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerPrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, rot, adjState, side
                );
                case FRAMED_CHECKERED_SLAB_SEGMENT -> testAgainstCheckeredSlabSegment(
                        dir, rot, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        dir, rot, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite())) ||
               SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstCornerSlopeEdge(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlopeEdgeDirs.CornerSlopeEdge.getCornerDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlabDirs.SlabEdge.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlabDirs.SlabCorner.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(StairsDirs.HalfStairs.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(StairsDirs.VerticalStairs.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(StairsDirs.VerticalHalfStairs.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_PILLAR)
    private static boolean testAgainstThreewayCornerPillar(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(PillarDirs.ThreewayCornerPillar.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_BOARD)
    private static boolean testAgainstCornerBoard(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(PaneDirs.CornerBoard.getCornerDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopeSlabDirs.SlopeSlab.getTriDir(adjDir, adjTop, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatSlopeSlabCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopeSlabDirs.FlatSlopeSlabCorner.getTriDir(adjDir, adjTop, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopeSlabDirs.FlatInnerSlopeSlabCorner.getTriDir(adjDir, adjTop, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.SlopePanel.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.FlatSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallInnerCornerSlopePanelWall.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanelWall.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallPrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallPrismCornerSlopePanelWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerPrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismCornerSlopePanelWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlabDirs.MasonryCornerSegment.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT)
    private static boolean testAgainstCheckeredSlabSegment(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlabDirs.CheckeredSlabSegment.getCornerDir(adjTop, adjSecond, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(dir, rot, side).isEqualTo(SlabDirs.CheckeredPanelSegment.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }
}
