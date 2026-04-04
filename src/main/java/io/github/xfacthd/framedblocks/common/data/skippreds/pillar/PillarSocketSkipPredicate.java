package io.github.xfacthd.framedblocks.common.data.skippreds.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.misc.MiscDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.pane.PaneDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slab.SlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slope.SlopeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge.SlopeEdgeDirs;
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
@CullTest(BlockType.FRAMED_PILLAR_SOCKET)
public final class PillarSocketSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(BlockStateProperties.FACING);

            return switch (blockType) {
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        dir, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_EDGE_SLAB -> testAgainstSlopeEdgeSlab(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_EDGE_PANEL -> testAgainstSlopeEdgePanel(
                        dir, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, adjState, side
                );
                case FRAMED_WALL -> testAgainstWall(
                        dir, adjState, side
                );
                case FRAMED_HALF_BOARD -> testAgainstHalfBoard(
                        dir, adjState, side
                );
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(
                        dir, adjState, side
                );
                case FRAMED_PILLAR -> testAgainstPillar(
                        dir, adjState, side
                );
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_SLAB -> testAgainstCompoundSlopeSlab(
                        dir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_PANEL -> testAgainstCompoundSlopePanel(
                        dir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, adjState, side
                );
                case FRAMED_PYRAMID -> testAgainstPyramid(
                        dir, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        dir, adjState, side
                );
                case FRAMED_UPPER_PYRAMID_SLAB -> testAgainstUpperPyramidSlab(
                        dir, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, adjState, side
                );
                case FRAMED_LAYERED_CUBE -> testAgainstLayeredCube(
                        dir, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(PillarDirs.PillarSocket.getHalfDir(adjDir, side.getOpposite())) ||
               (PillarDirs.PillarSocket.isPillarDir(dir, side) && PillarDirs.PillarSocket.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeDirs.HalfSlope.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeDirs.VerticalHalfSlope.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeEdgeDirs.SlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeEdgeDirs.ElevatedSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_SLAB)
    private static boolean testAgainstSlopeEdgeSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_PANEL)
    private static boolean testAgainstSlopeEdgePanel(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeEdgeDirs.SlopeEdgePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlabDirs.Slab.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlabDirs.SlabEdge.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlabDirs.Panel.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(PillarDirs.CornerPillar.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(StairsDirs.Stairs.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(StairsDirs.HalfStairs.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(StairsDirs.SlopedStairs.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(StairsDirs.VerticalStairs.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(StairsDirs.VerticalHalfStairs.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(
            Direction dir, BlockState adjState, Direction side
    ) {
        boolean adjUp = adjState.getValue(BlockStateProperties.UP);
        return (PillarDirs.PillarSocket.isPillarDir(dir, side) && PillarDirs.Wall.isPillarDir(adjUp, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_BOARD)
    private static boolean testAgainstHalfBoard(
            Direction dir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(PaneDirs.HalfBoard.getHalfDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(
            Direction dir, BlockState adjState, Direction side
    ) {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (PillarDirs.PillarSocket.isPillarDir(dir, side) && PillarDirs.ThickLattice.isPillarDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (PillarDirs.PillarSocket.isPillarDir(dir, side) && PillarDirs.Pillar.isPillarDir(adjAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (PillarDirs.PillarSocket.isPillarDir(dir, side) && PillarDirs.HalfPillar.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeSlabDirs.SlopeSlab.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeSlabDirs.ElevatedSlopeSlab.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_SLAB)
    private static boolean testAgainstCompoundSlopeSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeSlabDirs.CompoundSlopeSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeSlabDirs.FlatInnerSlopeSlabCorner.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeSlabDirs.FlatElevatedSlopeSlabCorner.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopePanelDirs.SlopePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_PANEL)
    private static boolean testAgainstCompoundSlopePanel(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopePanelDirs.CompoundSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PYRAMID)
    private static boolean testAgainstPyramid(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.PillarSocket.isPillarDir(dir, side) && SlopeDirs.Pyramid.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlopeDirs.ElevatedPyramidSlab.getHalfDir(adjDir, side.getOpposite())) ||
               (PillarDirs.PillarSocket.isPillarDir(dir, side) && SlopeDirs.ElevatedPyramidSlab.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_UPPER_PYRAMID_SLAB)
    private static boolean testAgainstUpperPyramidSlab(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.PillarSocket.isPillarDir(dir, side) && SlopeDirs.UpperPyramidSlab.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(SlabDirs.MasonryCornerSegment.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LAYERED_CUBE)
    private static boolean testAgainstLayeredCube(
            Direction dir, BlockState adjState, Direction side
    ) {
        Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
        int adjLayers = adjState.getValue(BlockStateProperties.LAYERS);

        return PillarDirs.PillarSocket.getHalfDir(dir, side).isEqualTo(MiscDirs.LayeredCube.getHalfDir(adjFacing, adjLayers, side.getOpposite()));
    }
}
