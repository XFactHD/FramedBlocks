package xfacthd.framedblocks.common.data.skippreds.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.*;
import xfacthd.framedblocks.common.data.skippreds.slope.*;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.*;

@CullTest(BlockType.FRAMED_STAIRS)
public final class StairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(StairBlock.FACING);
            StairsShape shape = state.getValue(StairBlock.SHAPE);
            Half half = state.getValue(StairBlock.HALF);

            return switch (type)
            {
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_MASONRY_CORNER_SEGMENT -> testAgainstMasonryCornerSegment(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
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
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CHECKERED_PANEL_SEGMENT -> testAgainstCheckeredPanelSegment(
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
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        if (getStairDir(dir, shape, half, side).isEqualTo(getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, shape, half, side).isEqualTo(getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_STAIRS)
    private static boolean testAgainstHalfStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getStairDir(dir, shape, half, side).isEqualTo(HalfStairsSkipPredicate.getStairDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, shape, half, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, shape, half, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_PANEL)
    private static boolean testAgainstPanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, shape, half, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_PILLAR)
    private static boolean testAgainstPillar(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, shape, half, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_CORNER)
    private static boolean testAgainstCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, shape, half, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_MASONRY_CORNER_SEGMENT)
    private static boolean testAgainstMasonryCornerSegment(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, shape, half, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getHalfDir(adjDir, side.getOpposite())) ||
                getCornerDir(dir, shape, half, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getCornerDir(adjDir, side.getOpposite())) ||
                getStairDir(dir, shape, half, side).isEqualTo(MasonryCornerSegmentSkipPredicate.getStairDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getStairDir(dir, shape, half, side).isEqualTo(VerticalStairsSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getStairDir(dir, shape, half, side).isEqualTo(VerticalHalfStairsSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, shape, half, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, shape, half, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, shape, half, side).isEqualTo(SmallCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, shape, half, side).isEqualTo(LargeCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, shape, half, side).isEqualTo(LargeCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, shape, half, side).isEqualTo(SmallInnerCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, shape, half, side).isEqualTo(LargeInnerCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, shape, half, side).isEqualTo(LargeInnerCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, shape, half, side).isEqualTo(ExtendedCornerSlopePanelSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, shape, half, side).isEqualTo(ExtendedInnerCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getStairDir(dir, shape, half, side).isEqualTo(ExtendedInnerCornerSlopePanelWallSkipPredicate.getStairDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, shape, half, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(VerticalHalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, shape, half, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT)
    private static boolean testAgainstCheckeredPanelSegment(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjSecond = adjState.getValue(PropertyHolder.SECOND);
        return getCornerDir(dir, shape, half, side).isEqualTo(CheckeredPanelSegmentSkipPredicate.getCornerDir(adjDir, adjSecond, side.getOpposite()));
    }



    public static TriangleDir getStairDir(Direction dir, StairsShape shape, Half half, Direction side)
    {
        Direction dirTwo = half == Half.TOP ? Direction.UP : Direction.DOWN;
        return switch (shape)
        {
            case STRAIGHT ->
            {
                if (side == dir.getClockWise() || side == dir.getCounterClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                yield TriangleDir.NULL;
            }
            case INNER_LEFT ->
            {
                if (side == dir.getOpposite())
                {
                    yield TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                }
                if (side == dir.getClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                if (side == dirTwo)
                {
                    yield TriangleDir.fromDirections(dir, dir.getCounterClockWise());
                }
                yield TriangleDir.NULL;
            }
            case INNER_RIGHT ->
            {
                if (side == dir.getOpposite())
                {
                    yield TriangleDir.fromDirections(dir.getClockWise(), dirTwo);
                }
                if (side == dir.getCounterClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                if (side == dirTwo)
                {
                    yield TriangleDir.fromDirections(dir, dir.getClockWise());
                }
                yield TriangleDir.NULL;
            }
            case OUTER_LEFT ->
            {
                if (side == dir)
                {
                    yield TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                }
                if (side == dir.getCounterClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                yield TriangleDir.NULL;
            }
            case OUTER_RIGHT ->
            {
                if (side == dir)
                {
                    yield TriangleDir.fromDirections(dir.getClockWise(), dirTwo);
                }
                if (side == dir.getClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                yield TriangleDir.NULL;
            }
        };
    }

    public static HalfDir getHalfDir(Direction dir, StairsShape shape, Half half, Direction side)
    {
        Direction edge = half == Half.TOP ? Direction.UP : Direction.DOWN;
        return switch (shape)
        {
            case INNER_LEFT, INNER_RIGHT -> HalfDir.NULL;
            case STRAIGHT ->
            {
                if (side == dir.getOpposite())
                {
                    yield HalfDir.fromDirections(side, edge);
                }
                else if (side == edge.getOpposite())
                {
                    yield HalfDir.fromDirections(side, dir);
                }
                yield HalfDir.NULL;
            }
            case OUTER_LEFT ->
            {
                if (side == dir.getOpposite() || side == dir.getClockWise())
                {
                    yield HalfDir.fromDirections(side, edge);
                }
                yield HalfDir.NULL;
            }
            case OUTER_RIGHT ->
            {
                if (side == dir.getOpposite() || side == dir.getCounterClockWise())
                {
                    yield HalfDir.fromDirections(side, edge);
                }
                yield HalfDir.NULL;
            }
        };
    }

    public static CornerDir getCornerDir(Direction dir, StairsShape shape, Half half, Direction side)
    {
        Direction normal = half == Half.TOP ? Direction.DOWN : Direction.UP;
        if (side != normal)
        {
            return CornerDir.NULL;
        }

        return switch (shape)
        {
            case STRAIGHT, INNER_LEFT, INNER_RIGHT -> CornerDir.NULL;
            case OUTER_LEFT -> CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            case OUTER_RIGHT -> CornerDir.fromDirections(side, dir, dir.getClockWise());
        };
    }
}