package io.github.xfacthd.framedblocks.common.data.skippreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.block.ISlopeBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.misc.MiscDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.pillar.PillarDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slab.SlabDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge.SlopeEdgeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
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
@CullTest(BlockType.FRAMED_HALF_SLOPE)
public final class HalfSlopeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            return switch (blockType)
            {
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLOPE -> testAgainstSlope(
                        dir, top, right, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_THREEWAY_CORNER,
                     FRAMED_PRISM_CORNER -> testAgainstThreewayCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER,
                     FRAMED_INNER_PRISM_CORNER -> testAgainstInnerThreewayCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, top, right, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, top, right, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, top, right, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_EDGE_SLAB -> testAgainstSlopeEdgeSlab(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_EDGE_PANEL -> testAgainstSlopeEdgePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, top, right, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, top, right, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE -> testAgainstRailSlope(
                        dir, top, right, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_PANEL -> testAgainstCompoundSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargePrismSlopePanelCornerWall(
                        dir, top, right, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerPrismSlopePanelCornerWall(
                        dir, top, right, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        dir, top, right, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, top, right, adjState, side
                );
                case FRAMED_LAYERED_CUBE -> testAgainstLayeredCube(
                        dir, top, right, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.HalfSlope.getTriDir(adjDir, adjTop, adjRight, side.getOpposite())) ||
               SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeDirs.HalfSlope.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE)
    private static boolean testAgainstSlope(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.Slope.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE)
    private static boolean testAgainstCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.Corner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE)
    private static boolean testAgainstInnerCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.InnerCorner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_THREEWAY_CORNER,
            BlockType.FRAMED_PRISM_CORNER
    })
    private static boolean testAgainstThreewayCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.ThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_INNER_THREEWAY_CORNER,
            BlockType.FRAMED_INNER_PRISM_CORNER
    })
    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.InnerThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeEdgeDirs.SlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeEdgeDirs.ElevatedSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_SLAB)
    private static boolean testAgainstSlopeEdgeSlab(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_PANEL)
    private static boolean testAgainstSlopeEdgePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeEdgeDirs.SlopeEdgePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlabDirs.SlabEdge.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlabDirs.Panel.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(PillarDirs.CornerPillar.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(StairsDirs.Stairs.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(StairsDirs.HalfStairs.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(StairsDirs.VerticalStairs.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getTriDir(adjDir, adjRot, side.getOpposite())) ||
               SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_RAIL_SLOPE,
            BlockType.FRAMED_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE
    })
    private static boolean testAgainstRailSlope(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = ((ISlopeBlock) adjState.getBlock()).getFacing(adjState);
        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopeDirs.RailSlope.getTriDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(PillarDirs.PillarSocket.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopePanelDirs.SlopePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_PANEL)
    private static boolean testAgainstCompoundSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopePanelDirs.CompoundSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargePrismSlopePanelCornerWall(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopePanelCornerDirs.LargePrismSlopePanelCornerWall.getBackTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerPrismSlopePanelCornerWall(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.HalfSlope.getTriDir(dir, top, right, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCornerWall.getFrontTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlopeDirs.ElevatedPyramidSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(SlabDirs.MasonryCornerSegment.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LAYERED_CUBE)
    private static boolean testAgainstLayeredCube(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
        int adjLayers = adjState.getValue(BlockStateProperties.LAYERS);

        return SlopeDirs.HalfSlope.getHalfDir(dir, top, right, side).isEqualTo(MiscDirs.LayeredCube.getHalfDir(adjFacing, adjLayers, side.getOpposite()));
    }
}
