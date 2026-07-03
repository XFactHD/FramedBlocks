package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SlopePanelCornerDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/// This class is machine-generated, any manual changes to this class will be overwritten.
@CullTest(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER)
public final class FlatExtendedInnerSlopePanelCornerSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);

            return switch (blockType) {
                case FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, rot, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, rot, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL -> testAgainstExtendedCornerSlopePanel(
                        dir, rot, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> testAgainstExtendedInnerCornerSlopePanel(
                        dir, rot, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargePrismCornerSlopePanel(
                        dir, rot, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargePrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstSmallInnerPrismCornerSlopePanel(
                        dir, rot, adjState, side
                );
                case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstSmallInnerPrismCornerSlopePanelWall(
                        dir, rot, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.ExtendedSlopePanel.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelDirs.FlatExtendedSlopePanelCorner.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedCornerSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.ExtendedCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    private static boolean testAgainstExtendedInnerCornerSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanel.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.ExtendedInnerCornerSlopePanelWall.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargePrismCornerSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargePrismCornerSlopePanel.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargePrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.LargePrismCornerSlopePanelWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstSmallInnerPrismCornerSlopePanel(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallInnerPrismCornerSlopePanel.getSideTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstSmallInnerPrismCornerSlopePanelWall(
            Direction dir, HorizontalRotation rot, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopePanelDirs.FlatExtendedInnerSlopePanelCorner.getTriDir(dir, rot, side).isEqualTo(SlopePanelCornerDirs.SmallInnerPrismCornerSlopePanelWall.getSideTriDir(adjDir, adjRot, side.getOpposite()));
    }
}
