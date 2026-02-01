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
import io.github.xfacthd.framedblocks.common.data.skippreds.slope.SlopeDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.stairs.StairsDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL)
public final class LargeInnerPrismSlopePanelCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargeInnerPrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SLOPE -> testAgainstSlope(
                        dir, top, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, top, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, top, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, top, adjState, side
                );
                case FRAMED_THREEWAY_CORNER,
                     FRAMED_PRISM_CORNER -> testAgainstThreewayCorner(
                        dir, top, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER,
                     FRAMED_INNER_PRISM_CORNER -> testAgainstInnerThreewayCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
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
                case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL -> testAgainstSmallPrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargePrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstSmallInnerPrismSlopePanelCorner(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite())) ||
               SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getBottomTriDir(adjDir, adjTop, side.getOpposite())) ||
               SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE)
    private static boolean testAgainstSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopeDirs.Slope.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopeDirs.VerticalHalfSlope.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE)
    private static boolean testAgainstCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopeDirs.Corner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE)
    private static boolean testAgainstInnerCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopeDirs.InnerCorner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_THREEWAY_CORNER,
            BlockType.FRAMED_PRISM_CORNER
    })
    private static boolean testAgainstThreewayCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopeDirs.ThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_INNER_THREEWAY_CORNER,
            BlockType.FRAMED_INNER_PRISM_CORNER
    })
    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopeDirs.InnerThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(StairsDirs.SlopedStairs.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE_PANEL)
    private static boolean testAgainstSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.SlopePanel.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.FlatSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.FlatInnerSlopePanelCorner.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargePrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargePrismSlopePanelCorner.getBottomTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getBottomTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getTopTriDir(adjDir, adjTop, side.getOpposite()));
    }
}
