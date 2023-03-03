package xfacthd.framedblocks.common.data.skippreds.slopepanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.PanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;

public final class SlopePanelSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir)
        {
            return !front && SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_PANEL -> testAgainstPanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_HALF_STAIRS, FRAMED_HALF_SLOPE -> testAgainstHalfStairs(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(level, pos, state, dir, rot, front, adjState, side);
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(level, pos, state, dir, rot, front, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, rot, front, side).isEqualTo(getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getTriDir(dir, rot, front, side).isEqualTo(getTriDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, rot, front, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstFlatSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getTriDir(dir, rot, front, side).isEqualTo(FlatSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, rot, front, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getTriDir(dir, rot, front, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, rot, front, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, rot, front, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, rot, front, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopePanelCorner(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstFlatSlopePanelCorner(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, rot, front, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstPanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, rot, front, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoublePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstPanel(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstCornerPillar(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, rot, front, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstCornerPillar(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        if (getHalfDir(dir, rot, front, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getHalfDir(dir, rot, front, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstCornerPillar(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getHalfDir(dir, rot, front, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstHalfStairs(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstHalfStairs(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        //Half slopes re-use the half stairs check
        return testAgainstHalfStairs(level, pos, state, dir, rot, front, states.getA(), side) ||
               testAgainstHalfStairs(level, pos, state, dir, rot, front, states.getB(), side);
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, HorizontalRotation rot, boolean front, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, rot, front, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }



    public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, boolean front, Direction side)
    {
        Direction perpRotDir = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir);
        if (side.getAxis() == perpRotDir.getAxis())
        {
            Direction shortEdge = rot.getOpposite().withFacing(dir);
            return HalfTriangleDir.fromDirections(dir, shortEdge, !front);
        }
        return HalfTriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, boolean front, Direction side)
    {
        if (side == rot.withFacing(dir).getOpposite())
        {
            return HalfDir.fromDirections(
                    side,
                    front ? dir.getOpposite() : dir
            );
        }
        return HalfDir.NULL;
    }
}
