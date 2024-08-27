package xfacthd.framedblocks.common.data.skippreds.slopeedge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.skippreds.CornerDir;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.pillar.ThreewayCornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.CheckeredPanelSegmentSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.CheckeredSlabSegmentSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.MasonryCornerSegmentSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.PanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slope.HalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slope.VerticalHalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.ExtendedSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.FlatExtendedSlopePanelCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.FlatInnerSlopePanelCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.ExtendedCornerSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.ExtendedCornerSlopePanelWallSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SmallCornerSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SmallCornerSlopePanelWallSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SmallInnerCornerSlopePanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SmallInnerCornerSlopePanelWallSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.ElevatedSlopeSlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.FlatElevatedSlopeSlabCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.FlatInnerSlopeSlabCornerSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.SlopeSlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.HalfStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.SlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.StairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalHalfStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalSlopedStairsSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.VerticalStairsSkipPredicate;

@CullTest(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
public final class ElevatedCornerSlopeEdgeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

            return switch (blockType)
            {
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, type, adjState, side
                );
                case FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE -> testAgainstElevatedInnerCornerSlopeEdge(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, type, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, type, adjState, side
                );
                case FRAMED_CORNER_SLOPE_EDGE -> testAgainstCornerSlopeEdge(
                        dir, type, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, type, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, type, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, type, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, type, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, type, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, type, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, type, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, type, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, type, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, type, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, type, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, type, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, type, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, type, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, type, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, type, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> testAgainstSmallInnerCornerSlopePanel(
                        dir, type, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, type, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, type, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, type, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, type, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, type, adjState, side
                );
                case FRAMED_CHECKERED_SLAB_SEGMENT -> testAgainstCheckeredSlabSegment(
                        dir, type, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
                        dir, type, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_PILLAR -> testAgainstThreewayCornerPillar(
                        dir, type, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, type, side).isEqualTo(getTriDir(adjDir, adjType, side.getOpposite())) ||
               getHalfDir(dir, type, side).isEqualTo(getHalfDir(adjDir, adjType, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedInnerCornerSlopeEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getTriDir(dir, type, side).isEqualTo(ElevatedInnerCornerSlopeEdgeSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getHalfDir(dir, type, side).isEqualTo(SlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return getTriDir(dir, type, side).isEqualTo(ElevatedSlopeEdgeSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite())) ||
               getHalfDir(dir, type, side).isEqualTo(ElevatedSlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstCornerSlopeEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getCornerDir(dir, type, side).isEqualTo(CornerSlopeEdgeSkipPredicate.getCornerDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getHalfDir(dir, type, side).isEqualTo(InnerCornerSlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getHalfDir(dir, type, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstSlabCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, type, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, type, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstCornerPillar(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, type, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return getHalfDir(dir, type, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getHalfDir(dir, type, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(dir, type, side).isEqualTo(SlopeSlabSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(dir, type, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, type, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, type, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, type, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, type, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, type, side).isEqualTo(SmallCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, type, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, type, side).isEqualTo(SmallInnerCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, type, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, type, side).isEqualTo(ExtendedCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getCornerDir(dir, type, side).isEqualTo(ExtendedCornerSlopePanelWallSkipPredicate.getCornerDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, type, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(VerticalHalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, type, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, type, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())) ||
               getCornerDir(dir, type, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT)
    private static boolean testAgainstCheckeredSlabSegment(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return getCornerDir(dir, type, side).isEqualTo(CheckeredSlabSegmentSkipPredicate.getCornerDir(adjTop, adjSecond, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);

        return getCornerDir(dir, type, side).isEqualTo(CheckeredPanelSegmentSkipPredicate.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_PILLAR)
    private static boolean testAgainstThreewayCornerPillar(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, type, side).isEqualTo(ThreewayCornerPillarSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }



    public static TriangleDir getTriDir(Direction dir, CornerType type, Direction side)
    {
        if (type.isHorizontal())
        {
            Direction yBack = type.isTop() ? Direction.UP : Direction.DOWN;
            Direction xBack = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            if (side == xBack)
            {
                return TriangleDir.fromDirections(dir, yBack);
            }
            if (side == yBack)
            {
                return TriangleDir.fromDirections(dir, xBack);
            }
        }
        else
        {
            if (side == dir)
            {
                Direction bottom = type == CornerType.TOP ? Direction.UP : Direction.DOWN;
                return TriangleDir.fromDirections(dir.getCounterClockWise(), bottom);
            }
            if (side == dir.getCounterClockWise())
            {
                Direction bottom = type == CornerType.TOP ? Direction.UP : Direction.DOWN;
                return TriangleDir.fromDirections(dir, bottom);
            }
        }
        return TriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, CornerType type, Direction side)
    {
        if (type.isHorizontal())
        {
            Direction yFront = type.isTop() ? Direction.DOWN : Direction.UP;
            Direction xFront = type.isRight() ? dir.getCounterClockWise() : dir.getClockWise();
            if (side == xFront || side == yFront)
            {
                return HalfDir.fromDirections(side, dir);
            }
        }
        else
        {
            if (side == dir.getOpposite() || side == dir.getClockWise())
            {
                Direction bottom = type == CornerType.TOP ? Direction.UP : Direction.DOWN;
                return HalfDir.fromDirections(side, bottom);
            }
        }
        return HalfDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, CornerType type, Direction side)
    {
        if (type.isHorizontal())
        {
            if (side == dir.getOpposite())
            {
                return CornerDir.fromDirections(
                        side,
                        type.isTop() ? Direction.UP : Direction.DOWN,
                        type.isRight() ? dir.getClockWise() : dir.getCounterClockWise()
                );
            }
        }
        else
        {
            Direction top = type == CornerType.TOP ? Direction.DOWN : Direction.UP;
            if (side == top)
            {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
        }
        return CornerDir.NULL;
    }
}
