package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
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
import io.github.xfacthd.framedblocks.common.data.skippreds.slope.SlopeDirs;
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
@CullTest(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
public final class FlatInnerSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            boolean front = state.getValue(PropertyHolder.FRONT);

            return switch (blockType)
            {
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, rot, front, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, rot, front, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, rot, front, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SLOPE_EDGE_SLAB -> testAgainstSlopeEdgeSlab(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SLOPE_EDGE_PANEL -> testAgainstSlopeEdgePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, rot, front, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, rot, front, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, rot, front, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_PANEL -> testAgainstCompoundSlopePanel(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
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
                case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL -> testAgainstSmallPrismSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstSmallPrismSlopePanelCornerWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargeInnerPrismSlopePanelCorner(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerPrismSlopePanelCornerWall(
                        dir, rot, front, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        dir, rot, front, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, rot, front, adjState, side
                );
                case FRAMED_LAYERED_CUBE -> testAgainstLayeredCube(
                        dir, rot, front, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite())) ||
               SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeDirs.HalfSlope.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeEdgeDirs.SlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeEdgeDirs.ElevatedSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeEdgeDirs.ElevatedCornerSlopeEdge.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_SLAB)
    private static boolean testAgainstSlopeEdgeSlab(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeEdgeDirs.SlopeEdgeSlab.getHalfDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE_PANEL)
    private static boolean testAgainstSlopeEdgePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeEdgeDirs.SlopeEdgePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlabDirs.SlabEdge.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlabDirs.Panel.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(PillarDirs.CornerPillar.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(StairsDirs.Stairs.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(StairsDirs.HalfStairs.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(StairsDirs.VerticalStairs.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(PillarDirs.PillarSocket.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.SlopePanel.getTriDir(adjDir, adjRot, adjFront, side.getOpposite())) ||
               SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.SlopePanel.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_PANEL)
    private static boolean testAgainstCompoundSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.CompoundSlopePanel.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.FlatSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.LargeCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.LargeCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.SmallInnerCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.SmallInnerCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallPrismSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallPrismSlopePanelCornerWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.SmallPrismSlopePanelCornerWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerPrismSlopePanelCorner(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerPrismSlopePanelCornerWall(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(dir, rot, front, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCornerWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlopeDirs.ElevatedPyramidSlab.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(SlabDirs.MasonryCornerSegment.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LAYERED_CUBE)
    private static boolean testAgainstLayeredCube(
            Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
        int adjLayers = adjState.getValue(BlockStateProperties.LAYERS);

        return SlopePanelDirs.FlatInnerSlopePanelCorner.getHalfDir(dir, rot, front, side).isEqualTo(MiscDirs.LayeredCube.getHalfDir(adjFacing, adjLayers, side.getOpposite()));
    }
}
