package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeedge.*;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_SLOPE_SLAB)
public final class SlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

            return switch (type)
            {
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_COMPOUND_SLOPE_SLAB -> testAgainstCompoundSlopeSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_FLAT_SLOPE_SLAB_CORNER -> testAgainstFlatSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, topHalf, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> testAgainstSmallCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> testAgainstLargeCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerCornerSlopePanelWall(
                        dir, top, topHalf, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_EDGE -> testAgainstElevatedSlopeEdge(
                        dir, topHalf, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, topHalf, adjState, side
                );
                case FRAMED_ELEVATED_CORNER_SLOPE_EDGE -> testAgainstElevatedCornerSlopeEdge(
                        dir, topHalf, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, topHalf, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, topHalf, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, topHalf, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS, FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfStairs(
                        dir, topHalf, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, topHalf, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_SLAB)
    private static boolean testAgainstSlopeSlab(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getTriDir(dir, topHalf, top, side).isEqualTo(getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, topHalf, side).isEqualTo(getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_COMPOUND_SLOPE_SLAB)
    private static boolean testAgainstCompoundSlopeSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, topHalf, side).isEqualTo(CompoundSlopeSlabSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getTriDir(dir, topHalf, top, side).isEqualTo(FlatSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        if (getHalfDir(dir, topHalf, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getTriDir(dir, topHalf, top, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTopHalf, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(SmallCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(LargeCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(SmallInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerCornerSlopePanelWall(
            Direction dir, boolean top, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, topHalf, top, side).isEqualTo(LargeInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getHalfDir(dir, topHalf, side).isEqualTo(SlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_SLOPE_EDGE)
    private static boolean testAgainstElevatedSlopeEdge(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return getHalfDir(dir, topHalf, side).isEqualTo(ElevatedSlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getHalfDir(dir, topHalf, side).isEqualTo(InnerCornerSlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedCornerSlopeEdge(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getHalfDir(dir, topHalf, side).isEqualTo(ElevatedCornerSlopeEdgeSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB)
    private static boolean testAgainstSlab(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        return getHalfDir(dir, topHalf, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLAB_EDGE)
    private static boolean testAgainstSlabEdge(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);

        return getHalfDir(dir, topHalf, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget({ BlockType.FRAMED_VERTICAL_HALF_STAIRS, BlockType.FRAMED_VERTICAL_HALF_SLOPE })
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, boolean topHalf, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, topHalf, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side)
    {
        if (side.getAxis() == dir.getClockWise().getAxis())
        {
            Direction longEdge = top ? Direction.UP : Direction.DOWN;
            return HalfTriangleDir.fromDirections(longEdge, dir, topHalf == top);
        }
        return HalfTriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, boolean topHalf, Direction side)
    {
        if (side == dir)
        {
            return HalfDir.fromDirections(
                    side,
                    topHalf ? Direction.UP : Direction.DOWN
            );
        }
        return HalfDir.NULL;
    }
}
