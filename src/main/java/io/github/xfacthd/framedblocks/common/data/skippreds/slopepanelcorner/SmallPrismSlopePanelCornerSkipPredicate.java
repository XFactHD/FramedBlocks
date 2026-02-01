package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopeedge.SlopeEdgeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL)
public final class SmallPrismSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (blockType)
            {
                case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL -> testAgainstSmallPrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SLOPE_EDGE -> testAgainstSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_CORNER_SLOPE_EDGE -> testAgainstCornerSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE_EDGE -> testAgainstInnerCornerSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstThreewayCornerSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE -> testAgainstInnerThreewayCornerSlopeEdge(
                        dir, top, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_CORNER_SLOPE_PANEL -> testAgainstSmallCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> testAgainstLargeInnerCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargePrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargeInnerPrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite())) ||
               SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_EDGE)
    private static boolean testAgainstSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopeEdgeDirs.SlopeEdge.getTriDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE_EDGE)
    private static boolean testAgainstCornerSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopeEdgeDirs.CornerSlopeEdge.getTriDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerCornerSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopeEdgeDirs.InnerCornerSlopeEdge.getTriDir(adjDir, adjType, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THREEWAY_CORNER_SLOPE_EDGE)
    private static boolean testAgainstThreewayCornerSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopeEdgeDirs.ThreewayCornerSlopeEdge.getTriDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE)
    private static boolean testAgainstInnerThreewayCornerSlopeEdge(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);
        boolean adjAlt = adjState.getValue(PropertyHolder.ALT_TYPE);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopeEdgeDirs.InnerThreewayCornerSlopeEdge.getTriDir(adjDir, adjTop, adjRight, adjAlt, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.SlopePanel.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.FlatSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargePrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargePrismSlopePanelCorner.getTopTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }
}
