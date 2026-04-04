package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

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
import io.github.xfacthd.framedblocks.common.data.skippreds.pillar.PillarDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slab.SlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge.SlopeEdgeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SlopePanelCornerDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.stairs.StairsDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_CORNER_BOARD)
public final class CornerBoardSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);

            return switch (blockType) {
                case FRAMED_CORNER_BOARD -> testAgainstCornerBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_CORNER_SLOPE_EDGE -> testAgainstCornerSlopeEdge(
                        cmpDir, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        cmpDir, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        cmpDir, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        cmpDir, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        cmpDir, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        cmpDir, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        cmpDir, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        cmpDir, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        cmpDir, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_PILLAR -> testAgainstThreewayCornerPillar(
                        cmpDir, adjState, side
                );
                case FRAMED_HALF_BOARD -> testAgainstHalfBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_INNER_CORNER_BOARD -> testAgainstInnerCornerBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        cmpDir, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        cmpDir, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        cmpDir, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        cmpDir, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        cmpDir, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        cmpDir, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        cmpDir, adjState, side
                );
                case FRAMED_CHECKERED_SLAB_SEGMENT -> testAgainstCheckeredSlabSegment(
                        cmpDir, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        cmpDir, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_BOARD)
    private static boolean testAgainstCornerBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.CornerBoard.getHalfEdgeDir(cmpDir, side).isEqualTo(PaneDirs.CornerBoard.getHalfEdgeDir(adjCmpDir, side.getOpposite())) ||
               PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(PaneDirs.CornerBoard.getCornerDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstCornerSlopeEdge(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopeEdgeDirs.CornerSlopeEdge.getCornerDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlabDirs.SlabEdge.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlabDirs.SlabCorner.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(PillarDirs.CornerPillar.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(StairsDirs.Stairs.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(StairsDirs.HalfStairs.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(StairsDirs.VerticalStairs.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(StairsDirs.VerticalHalfStairs.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_PILLAR)
    private static boolean testAgainstThreewayCornerPillar(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(PillarDirs.ThreewayCornerPillar.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_BOARD)
    private static boolean testAgainstHalfBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.CornerBoard.getHalfEdgeDir(cmpDir, side).isEqualTo(PaneDirs.HalfBoard.getHalfEdgeDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_BOARD)
    private static boolean testAgainstInnerCornerBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.CornerBoard.getHalfEdgeDir(cmpDir, side).isEqualTo(PaneDirs.InnerCornerBoard.getHalfEdgeDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanel.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanelWall.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.SmallInnerCornerSlopePanel.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.SmallInnerCornerSlopePanelWall.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanel.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanelWall.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlabDirs.MasonryCornerSegment.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT)
    private static boolean testAgainstCheckeredSlabSegment(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlabDirs.CheckeredSlabSegment.getCornerDir(adjTop, adjSecond, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return PaneDirs.CornerBoard.getCornerDir(cmpDir, side).isEqualTo(SlabDirs.CheckeredPanelSegment.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }
}
