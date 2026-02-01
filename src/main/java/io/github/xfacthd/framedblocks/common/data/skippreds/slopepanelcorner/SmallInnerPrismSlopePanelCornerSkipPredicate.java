package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel.SlopePanelDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL)
public final class SmallInnerPrismSlopePanelCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstSmallInnerPrismSlopePanelCorner(
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
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
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

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite())) ||
               SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getTopTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargePrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getSideTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargePrismSlopePanelCorner.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerPrismSlopePanelCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelCornerDirs.SmallInnerPrismSlopePanelCorner.getTopTriDir(dir, top, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getBottomTriDir(adjDir, adjTop, side.getOpposite()));
    }
}
