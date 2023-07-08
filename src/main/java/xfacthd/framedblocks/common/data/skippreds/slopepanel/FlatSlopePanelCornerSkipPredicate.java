package xfacthd.framedblocks.common.data.skippreds.slopepanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;

public final class FlatSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir)
        {
            return !front && SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);

            return switch (type)
            {
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        dir, front, rot, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, front, rot, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        dir, front, rot, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        dir, front, rot, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, front, rot, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        dir, front, rot, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getTriDir(dir, rot, front, side).isEqualTo(getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getTriDir(dir, rot, front, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, front, rot, states.getA(), side) ||
               testAgainstFlatSlopePanelCorner(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, front, rot, states.getA(), side) ||
               testAgainstFlatSlopePanelCorner(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopePanelCorner(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatSlopePanelCorner(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getTriDir(dir, rot, front, side).isEqualTo(SlopePanelSkipPredicate.getTriDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopePanel(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, front, rot, states.getA(), side) ||
               testAgainstSlopePanel(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, front, rot, states.getA(), side) ||
               testAgainstSlopePanel(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, front, rot, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            Direction dir, boolean front, HorizontalRotation rot, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, front, rot, states.getB(), side);
    }



    public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, boolean front, Direction side)
    {
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
        {
            Direction shortEdge = side == rotDir.getOpposite() ? perpRotDir.getOpposite() : rotDir.getOpposite();
            return HalfTriangleDir.fromDirections(dir, shortEdge, !front);
        }
        return HalfTriangleDir.NULL;
    }
}
