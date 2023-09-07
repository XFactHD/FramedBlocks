package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;
import xfacthd.framedblocks.common.data.skippreds.slopepanelcorner.*;

@CullTest(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER)
public final class FlatElevatedInnerSlopeSlabCornerSkipPredicate implements SideSkipPredicate
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
                case FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerSlopeSlabCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
                        dir, top, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, top, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> testAgainstExtendedCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedDoubleCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> testAgainstExtendedInnerDoubleCornerSlopePanelWall(
                        dir, top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedInnerSlopeSlabCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER,
            partTargets = BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER
    )
    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(dir, top, states.getA(), side) ;
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER,
            partTargets = BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER
    )
    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedInnerSlopeSlabCorner(dir, top, states.getA(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getTriDir(dir, top, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB,
            partTargets = BlockType.FRAMED_ELEVATED_SLOPE_SLAB
    )
    private static boolean testAgainstElevatedDoubleSlopeSlab(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(dir, top, states.getA(), side);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(ExtendedCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    private static boolean testAgainstExtendedInnerCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, top, side).isEqualTo(ExtendedInnerCornerSlopePanelWallSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedDoubleCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedCornerSlopePanelWall(dir, top, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
            partTargets = BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W
    )
    private static boolean testAgainstExtendedInnerDoubleCornerSlopePanelWall(
            Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedInnerCornerSlopePanelWall(dir, top, states.getA(), side);
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side)
    {
        if (side == dir.getOpposite() || side == dir.getClockWise())
        {
            Direction longEdge = top ? Direction.UP : Direction.DOWN;
            Direction shortEdge = side == dir.getOpposite() ? dir.getCounterClockWise() : dir;
            return HalfTriangleDir.fromDirections(longEdge, shortEdge, false);
        }
        return HalfTriangleDir.NULL;
    }
}
