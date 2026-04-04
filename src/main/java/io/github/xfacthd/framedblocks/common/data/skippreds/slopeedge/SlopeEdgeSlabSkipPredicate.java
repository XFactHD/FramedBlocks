package io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge;

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
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeslab.SlopeSlabDirs;
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
@CullTest(BlockType.FRAMED_SLOPE_EDGE_SLAB)
public final class SlopeEdgeSlabSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (blockType) {
                case FRAMED_SLOPE_EDGE_SLAB -> testAgainstSlopeEdgeSlab(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLOPE_EDGE_PANEL -> testAgainstSlopeEdgePanel(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_HALF_BOARD -> testAgainstHalfBoard(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_SLAB -> testAgainstCompoundSlopeSlab(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_PANEL -> testAgainstCompoundSlopePanel(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, topHalf, top, adjState, side
                );
                case FRAMED_LAYERED_CUBE -> testAgainstLayeredCube(
                        dir, topHalf, top, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_SLAB)
    private static boolean testAgainstSlopeEdgeSlab(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(adjDir, adjTopHalf, adjTop, side.getOpposite())) ||
               SlopeEdgeDirs.SlopeEdgeSlab.getTriDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.SlopeEdgeSlab.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeDirs.HalfSlope.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeDirs.VerticalHalfSlope.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.SlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.ElevatedSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_PANEL)
    private static boolean testAgainstSlopeEdgePanel(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeEdgeDirs.SlopeEdgePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlabDirs.Slab.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlabDirs.SlabEdge.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlabDirs.Panel.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(StairsDirs.Stairs.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(StairsDirs.HalfStairs.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(StairsDirs.SlopedStairs.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(StairsDirs.VerticalStairs.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(StairsDirs.VerticalHalfStairs.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_BOARD)
    private static boolean testAgainstHalfBoard(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(PaneDirs.HalfBoard.getHalfDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(PillarDirs.PillarSocket.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeSlabDirs.SlopeSlab.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeSlabDirs.ElevatedSlopeSlab.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_SLAB)
    private static boolean testAgainstCompoundSlopeSlab(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeSlabDirs.CompoundSlopeSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeSlabDirs.FlatInnerSlopeSlabCorner.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeSlabDirs.FlatElevatedSlopeSlabCorner.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopePanelDirs.SlopePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_PANEL)
    private static boolean testAgainstCompoundSlopePanel(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopePanelDirs.CompoundSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlopeDirs.ElevatedPyramidSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(SlabDirs.MasonryCornerSegment.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LAYERED_CUBE)
    private static boolean testAgainstLayeredCube(
            Direction dir, boolean topHalf, boolean top, BlockState adjState, Direction side
    ) {
        Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
        int adjLayers = adjState.getValue(BlockStateProperties.LAYERS);

        return SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(dir, topHalf, top, side).isEqualTo(MiscDirs.LayeredCube.getHalfDir(adjFacing, adjLayers, side.getOpposite()));
    }
}
