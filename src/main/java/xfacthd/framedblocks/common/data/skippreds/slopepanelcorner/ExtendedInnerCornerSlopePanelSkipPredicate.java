package xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

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
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.slopeedge.ElevatedInnerCornerSlopeEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopeedge.InnerCornerSlopeEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

@CullTest(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
public final class ExtendedInnerCornerSlopePanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (type)
            {
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_LARGE_CORNER_SLOPE_PANEL -> testAgainstLargeCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> testAgainstLargeInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE -> testAgainstElevatedInnerCornerSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, side).isEqualTo(getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        if (getStairDir(dir, top, side).isEqualTo(getStairDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, side).isEqualTo(LargeCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, side).isEqualTo(LargeInnerCornerSlopePanelSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(ExtendedCornerSlopePanelSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(FlatExtendedInnerSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return getStairDir(dir, top, side).isEqualTo(InnerCornerSlopeEdgeSkipPredicate.getStairDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstElevatedInnerCornerSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return getStairDir(dir, top, side).isEqualTo(ElevatedInnerCornerSlopeEdgeSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_STAIRS)
    private static boolean testAgainstStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        return getStairDir(dir, top, side).isEqualTo(StairsSkipPredicate.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_STAIRS)
    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        return getStairDir(dir, top, side).isEqualTo(VerticalStairsSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_STAIRS)
    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getStairDir(dir, top, side).isEqualTo(VerticalHalfStairsSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite()));
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side)
    {
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dir)
        {
            return HalfTriangleDir.fromDirections(dir.getClockWise(), dirTwo, false);
        }
        else if (side == dir.getCounterClockWise())
        {
            return HalfTriangleDir.fromDirections(dir.getOpposite(), dirTwo, false);
        }
        return HalfTriangleDir.NULL;
    }

    public static TriangleDir getStairDir(Direction dir, boolean top, Direction side)
    {
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            return TriangleDir.fromDirections(dir.getOpposite(), dir.getClockWise());
        }
        return TriangleDir.NULL;
    }
}
