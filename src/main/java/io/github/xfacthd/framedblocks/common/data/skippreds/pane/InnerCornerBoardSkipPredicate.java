package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
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

/// This class is machine-generated, any manual changes to this class will be overwritten.
@CullTest(BlockType.FRAMED_INNER_CORNER_BOARD)
public final class InnerCornerBoardSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);

            return switch (blockType) {
                case FRAMED_INNER_CORNER_BOARD -> testAgainstInnerCornerBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        cmpDir, adjState, side
                );
                case FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE -> testAgainstElevatedInnerCornerSlopeEdge(
                        cmpDir, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstInnerThreewayCornerSlopeEdge(
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
                case FRAMED_BOARD -> testAgainstBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_HALF_BOARD -> testAgainstHalfBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_CORNER_BOARD -> testAgainstCornerBoard(
                        cmpDir, adjState, side
                );
                case FRAMED_CORNER_STRIP -> testAgainstCornerStrip(
                        cmpDir, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL -> testAgainstLargeCornerSlopePanel(
                        cmpDir, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        cmpDir, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> testAgainstLargeInnerCornerSlopePanel(
                        cmpDir, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        cmpDir, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
                        cmpDir, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerCornerSlopePanelWall(
                        cmpDir, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        cmpDir, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_BOARD)
    private static boolean testAgainstInnerCornerBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.InnerCornerBoard.getEdgeDir(cmpDir, side).isEqualTo(PaneDirs.InnerCornerBoard.getEdgeDir(adjCmpDir, side.getOpposite())) ||
               PaneDirs.InnerCornerBoard.getHalfEdgeDir(cmpDir, side).isEqualTo(PaneDirs.InnerCornerBoard.getHalfEdgeDir(adjCmpDir, side.getOpposite())) ||
               PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(PaneDirs.InnerCornerBoard.getStairDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getStairDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedInnerCornerSlopeEdge(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopeEdgeDirs.ElevatedInnerCornerSlopeEdge.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerThreewayCornerSlopeEdge(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopeEdgeDirs.InnerThreewayCornerSlopeEdge.getStairDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(StairsDirs.Stairs.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(StairsDirs.HalfStairs.getStairDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(StairsDirs.VerticalStairs.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(StairsDirs.VerticalHalfStairs.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_PILLAR)
    private static boolean testAgainstThreewayCornerPillar(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(PillarDirs.ThreewayCornerPillar.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_BOARD)
    private static boolean testAgainstBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        int adjFaces = adjState.getValue(PropertyHolder.FACES);
        return PaneDirs.InnerCornerBoard.getEdgeDir(cmpDir, side).isEqualTo(PaneDirs.Board.getSingleEdgeDir(adjFaces, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_BOARD)
    private static boolean testAgainstHalfBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.InnerCornerBoard.getEdgeDir(cmpDir, side).isEqualTo(PaneDirs.HalfBoard.getEdgeDir(adjCmpDir, side.getOpposite())) ||
               PaneDirs.InnerCornerBoard.getHalfEdgeDir(cmpDir, side).isEqualTo(PaneDirs.HalfBoard.getHalfEdgeDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_BOARD)
    private static boolean testAgainstCornerBoard(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PaneDirs.InnerCornerBoard.getHalfEdgeDir(cmpDir, side).isEqualTo(PaneDirs.CornerBoard.getHalfEdgeDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_STRIP)
    private static boolean testAgainstCornerStrip(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return PaneDirs.InnerCornerBoard.getEdgeDir(cmpDir, side).isEqualTo(PaneDirs.CornerStrip.getEdgeDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.LargeCornerSlopePanel.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.LargeCornerSlopePanelWall.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanel.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanelWall.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanel.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanelWall.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PaneDirs.InnerCornerBoard.getStairDir(cmpDir, side).isEqualTo(SlabDirs.MasonryCornerSegment.getStairDir(adjDir, adjTop, side.getOpposite()));
    }
}
