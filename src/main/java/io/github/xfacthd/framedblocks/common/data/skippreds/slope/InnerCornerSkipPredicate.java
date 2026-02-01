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
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.SlopePanelCornerDirs;
import io.github.xfacthd.framedblocks.common.data.skippreds.stairs.StairsDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_INNER_CORNER_SLOPE)
public final class InnerCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPE -> testAgainstSlope(
                        dir, type, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, type, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        dir, type, adjState, side
                );
                case FRAMED_THREEWAY_CORNER,
                     FRAMED_PRISM_CORNER -> testAgainstThreewayCorner(
                        dir, type, adjState, side
                );
                case FRAMED_INNER_THREEWAY_CORNER,
                     FRAMED_INNER_PRISM_CORNER -> testAgainstInnerThreewayCorner(
                        dir, type, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, type, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, type, adjState, side
                );
                case FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE -> testAgainstRailSlope(
                        dir, type, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargePrismSlopePanelCorner(
                        dir, type, adjState, side
                );
                case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargePrismSlopePanelCornerWall(
                        dir, type, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL -> testAgainstLargeInnerPrismSlopePanelCorner(
                        dir, type, adjState, side
                );
                case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W -> testAgainstLargeInnerPrismSlopePanelCornerWall(
                        dir, type, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_INNER_CORNER_SLOPE)
    private static boolean testAgainstInnerCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.InnerCorner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPE)
    private static boolean testAgainstSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.Slope.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_SLOPE)
    private static boolean testAgainstHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.HalfSlope.getTriDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_HALF_SLOPE)
    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.VerticalHalfSlope.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_SLOPE)
    private static boolean testAgainstCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.Corner.getTriDir(adjDir, adjType, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_THREEWAY_CORNER,
            BlockType.FRAMED_PRISM_CORNER
    })
    private static boolean testAgainstThreewayCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.ThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_INNER_THREEWAY_CORNER,
            BlockType.FRAMED_INNER_PRISM_CORNER
    })
    private static boolean testAgainstInnerThreewayCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.InnerThreewayCorner.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_STAIRS)
    private static boolean testAgainstSlopedStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(StairsDirs.SlopedStairs.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(StairsDirs.VerticalSlopedStairs.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_RAIL_SLOPE,
            BlockType.FRAMED_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE
    })
    private static boolean testAgainstRailSlope(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = ((ISlopeBlock) adjState.getBlock()).getFacing(adjState);
        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopeDirs.RailSlope.getTriDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargePrismSlopePanelCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopePanelCornerDirs.LargePrismSlopePanelCorner.getBottomTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargePrismSlopePanelCornerWall(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopePanelCornerDirs.LargePrismSlopePanelCornerWall.getBackTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL)
    private static boolean testAgainstLargeInnerPrismSlopePanelCorner(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCorner.getTopTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstLargeInnerPrismSlopePanelCornerWall(
            Direction dir, CornerType type, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return SlopeDirs.InnerCorner.getTriDir(dir, type, side).isEqualTo(SlopePanelCornerDirs.LargeInnerPrismSlopePanelCornerWall.getFrontTriDir(adjDir, adjRot, side.getOpposite()));
    }
}
