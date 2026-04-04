package io.github.xfacthd.framedblocks.common.data.skippreds.stairs;

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
import io.github.xfacthd.framedblocks.common.data.skippreds.misc.MiscDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.pane.PaneDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.pillar.PillarDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slab.SlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slope.SlopeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge.SlopeEdgeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SlopePanelCornerDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeslab.SlopeSlabDirs;
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
@CullTest(BlockType.FRAMED_STAIRS)
public final class StairsSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            Half half = state.getValue(BlockStateProperties.HALF);

            return switch (blockType) {
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CORNER_SLOPE_EDGE -> testAgainstCornerSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE -> testAgainstElevatedInnerCornerSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstInnerThreewayCornerSlopeEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_EDGE_SLAB -> testAgainstSlopeEdgeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_EDGE_PANEL -> testAgainstSlopeEdgePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, shape, half, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_PILLAR -> testAgainstThreewayCornerPillar(
                        dir, shape, half, adjState, side
                );
                case FRAMED_HALF_BOARD -> testAgainstHalfBoard(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CORNER_BOARD -> testAgainstCornerBoard(
                        dir, shape, half, adjState, side
                );
                case FRAMED_INNER_CORNER_BOARD -> testAgainstInnerCornerBoard(
                        dir, shape, half, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_SLAB -> testAgainstCompoundSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_PANEL -> testAgainstCompoundSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL -> testAgainstLargeCornerSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> testAgainstLargeInnerCornerSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, shape, half, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerCornerSlopePanelWall(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        dir, shape, half, adjState, side
                );
                case FRAMED_LAYERED_CUBE -> testAgainstLayeredCube(
                        dir, shape, half, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(StairsDirs.Stairs.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())) ||
               StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(StairsDirs.Stairs.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())) ||
               StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(StairsDirs.Stairs.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeDirs.HalfSlope.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeDirs.VerticalHalfSlope.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.SlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.ElevatedSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstCornerSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.CornerSlopeEdge.getCornerDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getStairDir(adjDir, adjType, adjAlt, side.getOpposite())) ||
               StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite())) ||
               StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedInnerCornerSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.ElevatedInnerCornerSlopeEdge.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerThreewayCornerSlopeEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.InnerThreewayCornerSlopeEdge.getStairDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_SLAB)
    private static boolean testAgainstSlopeEdgeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_PANEL)
    private static boolean testAgainstSlopeEdgePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeEdgeDirs.SlopeEdgePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlabDirs.Slab.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlabDirs.SlabEdge.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlabDirs.SlabCorner.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlabDirs.Panel.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(PillarDirs.CornerPillar.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(StairsDirs.HalfStairs.getStairDir(adjDir, adjTop, adjRight, side.getOpposite())) ||
               StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(StairsDirs.HalfStairs.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())) ||
               StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(StairsDirs.HalfStairs.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(StairsDirs.SlopedStairs.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(StairsDirs.VerticalStairs.getStairDir(adjDir, adjType, side.getOpposite())) ||
               StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(StairsDirs.VerticalStairs.getHalfDir(adjDir, adjType, side.getOpposite())) ||
               StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(StairsDirs.VerticalStairs.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(StairsDirs.VerticalHalfStairs.getStairDir(adjDir, adjTop, side.getOpposite())) ||
               StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(StairsDirs.VerticalHalfStairs.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_PILLAR)
    private static boolean testAgainstThreewayCornerPillar(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(PillarDirs.ThreewayCornerPillar.getStairDir(adjDir, adjTop, side.getOpposite())) ||
               StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(PillarDirs.ThreewayCornerPillar.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_BOARD)
    private static boolean testAgainstHalfBoard(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(PaneDirs.HalfBoard.getHalfDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_BOARD)
    private static boolean testAgainstCornerBoard(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(PaneDirs.CornerBoard.getCornerDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_BOARD)
    private static boolean testAgainstInnerCornerBoard(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(PaneDirs.InnerCornerBoard.getStairDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(PillarDirs.PillarSocket.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabDirs.SlopeSlab.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabDirs.ElevatedSlopeSlab.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_SLAB)
    private static boolean testAgainstCompoundSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabDirs.CompoundSlopeSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabDirs.FlatInnerSlopeSlabCorner.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabDirs.FlatElevatedSlopeSlabCorner.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelDirs.SlopePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_PANEL)
    private static boolean testAgainstCompoundSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelDirs.CompoundSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanel.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.LargeCornerSlopePanel.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.LargeCornerSlopePanelWall.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.SmallInnerCornerSlopePanel.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanel.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanelWall.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanel.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanel.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanelWall.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlopeDirs.ElevatedPyramidSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return StairsDirs.Stairs.getStairDir(dir, shape, half, side).isEqualTo(SlabDirs.MasonryCornerSegment.getStairDir(adjDir, adjTop, side.getOpposite())) ||
               StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(SlabDirs.MasonryCornerSegment.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return StairsDirs.Stairs.getCornerDir(dir, shape, half, side).isEqualTo(SlabDirs.CheckeredPanelSegment.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LAYERED_CUBE)
    private static boolean testAgainstLayeredCube(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    ) {
        Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
        int adjLayers = adjState.getValue(BlockStateProperties.LAYERS);

        return StairsDirs.Stairs.getHalfDir(dir, shape, half, side).isEqualTo(MiscDirs.LayeredCube.getHalfDir(adjFacing, adjLayers, side.getOpposite()));
    }
}
