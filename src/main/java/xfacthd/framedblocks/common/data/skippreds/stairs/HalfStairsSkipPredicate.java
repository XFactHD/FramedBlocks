package xfacthd.framedblocks.common.data.skippreds.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.*;
import xfacthd.framedblocks.common.data.skippreds.slope.HalfSlopeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;

public final class HalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            return switch (type)
            {
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        dir, top, right, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstSlabCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        dir, top, right, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, top, right, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getStairDir(dir, top, right, side).isEqualTo(getStairDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, top, right, side).isEqualTo(getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, top, right, side).isEqualTo(getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, top, right, states.getA(), side) ||
               testAgainstHalfStairs(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        if (getStairDir(dir, top, right, side).isEqualTo(StairsSkipPredicate.getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, top, right, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, top, right, side).isEqualTo(StairsSkipPredicate.getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, top, right, states.getA(), side) ||
               testAgainstSlabEdge(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstVerticalStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getStairDir(dir, top, right, side).isEqualTo(VerticalStairsSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, top, right, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, top, right, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(dir, top, right, states.getA(), side) ||
               testAgainstCornerPillar(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, right, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalDividedStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, top, right, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstSlabEdge(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, right, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, top, right, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, top, right, states.getA(), side) ||
               testAgainstSlabEdge(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(dir, top, right, states.getA(), side) ||
               testAgainstSlabEdge(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstCornerPillar(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, top, right, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, top, right, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(dir, top, right, states.getA(), side) ||
               testAgainstCornerPillar(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstSlabCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, top, right, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstPanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, top, right, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    private static boolean testAgainstDoublePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, top, right, states.getA(), side) ||
               testAgainstPanel(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, top, right, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, top, right, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, top, right, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, top, right, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, top, right, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, top, right, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, top, right, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, top, right, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, top, right, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, top, right, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlope(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return false;
        }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, top, right, states.getA(), side) ||
               testAgainstHalfSlope(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, top, right, states.getA(), side) ||
               testAgainstHalfSlope(dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, top, right, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }



    public static TriangleDir getStairDir(Direction dir, boolean top, boolean right, Direction side)
    {
        if ((!right && side == dir.getCounterClockWise()) || (right && side == dir.getClockWise()))
        {
            return TriangleDir.fromDirections(
                    dir,
                    top ? Direction.UP : Direction.DOWN
            );
        }
        return TriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, boolean top, boolean right, Direction side)
    {
        if (side == dir || (!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return HalfDir.fromDirections(
                    side,
                    right ? dir.getClockWise() : dir.getCounterClockWise()
            );
        }
        return HalfDir.NULL;
    }

    public static CornerDir getCornerDir(Direction dir, boolean top, boolean right, Direction side)
    {
        if (side == dir.getOpposite())
        {
            return CornerDir.fromDirections(
                    side,
                    top ? Direction.UP : Direction.DOWN,
                    right ? dir.getClockWise() : dir.getCounterClockWise()
            );
        }
        else if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            return CornerDir.fromDirections(
                    side,
                    dir,
                    right ? dir.getClockWise() : dir.getCounterClockWise()
            );
        }
        return CornerDir.NULL;
    }
}
