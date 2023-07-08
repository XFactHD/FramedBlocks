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
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.*;
import xfacthd.framedblocks.common.data.skippreds.slope.*;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.slopeslab.*;

public final class StairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(StairBlock.FACING);
            StairsShape shape = state.getValue(StairBlock.SHAPE);
            Half half = state.getValue(StairBlock.HALF);

            return switch (type)
            {
                case FRAMED_STAIRS -> testAgainstStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB -> testAgainstSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstEdge(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        dir, shape, half, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstPillar(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLAB_CORNER -> testAgainstCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_DIVIDED_STAIRS -> testAgainstVerticalDividedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_SLAB -> testAgainstSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstDoubleSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_STACKED_SLOPE_SLAB -> testAgainstStackedSlopeSlab(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatInnerSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatDoubleSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatInverseDoubleSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER -> testAgainstFlatStackedSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatStackedInnerSlopeSlabCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        dir, shape, half, adjState, side
                );
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(
                        dir, shape, half, adjState, side
                );
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(
                        dir, shape, half, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        dir, shape, half, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        if (getStairDir(dir, shape, half, side).isEqualTo(getStairDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, shape, half, side).isEqualTo(getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(getCornerDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(dir, shape, half, states.getA(), side) ||
               testAgainstEdge(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getStairDir(dir, shape, half, side).isEqualTo(HalfStairsSkipPredicate.getStairDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, shape, half, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(HalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(dir, shape, half, states.getA(), side) ||
               testAgainstHalfStairs(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlabSkipPredicate.getHalfDir(adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, shape, half, states.getA(), side) ||
               testAgainstSlab(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstEdge(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, shape, half, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(SlabEdgeSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, shape, half, states.getA(), side) ||
               testAgainstEdge(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstEdge(dir, shape, half, states.getA(), side) ||
               testAgainstEdge(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstPanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        return getHalfDir(dir, shape, half, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite()));
    }

    private static boolean testAgainstDoublePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, shape, half, states.getA(), side) ||
               testAgainstPanel(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstPillar(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, shape, half, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(CornerPillarSkipPredicate.getCornerDir(adjDir, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPillar(dir, shape, half, states.getA(), side) ||
               testAgainstPillar(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getCornerDir(dir, shape, half, side).isEqualTo(SlabCornerSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getStairDir(dir, shape, half, side).isEqualTo(VerticalStairsSkipPredicate.getStairDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(VerticalStairsSkipPredicate.getCornerDir(adjDir, adjType, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(dir, shape, half, states.getA(), side) ||
               testAgainstPillar(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstVerticalHalfStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getStairDir(dir, shape, half, side).isEqualTo(VerticalHalfStairsSkipPredicate.getStairDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getHalfDir(dir, shape, half, side).isEqualTo(VerticalHalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        else if (getCornerDir(dir, shape, half, side).isEqualTo(VerticalHalfStairsSkipPredicate.getCornerDir(adjDir, adjTop, side.getOpposite())))
        {
            return true;
        }
        return false;
    }

    private static boolean testAgainstVerticalDividedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfStairs(dir, shape, half, states.getA(), side) ||
               testAgainstVerticalHalfStairs(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlopeSlabSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    private static boolean testAgainstElevatedSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, shape, half, states.getA(), side) ||
               testAgainstSlopeSlab(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopeSlab(dir, shape, half, states.getA(), side) ||
               testAgainstSlopeSlab(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(dir, shape, half, states.getA(), side) ||
               testAgainstSlopeSlab(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopeSlab(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, shape, half, states.getA(), side) ||
               testAgainstSlopeSlab(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatInnerSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTopHalf, side.getOpposite()));
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, shape, half, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopeSlabCorner(dir, shape, half, states.getA(), side);
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(dir, shape, half, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, shape, half, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopeSlabCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlab(dir, shape, half, states.getA(), side) ||
               testAgainstFlatInnerSlopeSlabCorner(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstExtendedSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, shape, half, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstDoubleSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, shape, half, states.getA(), side) ||
               testAgainstSlopePanel(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(dir, shape, half, states.getA(), side) ||
               testAgainstSlopePanel(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, shape, half, states.getA(), side) ||
               testAgainstPanel(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, shape, half, states.getA(), side) ||
               testAgainstSlopePanel(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite()));
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, shape, half, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, shape, half, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(dir, shape, half, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(dir, shape, half, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, shape, half, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(dir, shape, half, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        return getHalfDir(dir, shape, half, side).isEqualTo(HalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite()));
    }

    private static boolean testAgainstVerticalHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(VerticalHalfSlopeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstDividedSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        if (adjState.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return testAgainstVerticalHalfSlope(dir, shape, half, states.getA(), side) ||
                   testAgainstVerticalHalfSlope(dir, shape, half, states.getB(), side);
        }
        else
        {
            return testAgainstHalfSlope(dir, shape, half, states.getA(), side) ||
                   testAgainstHalfSlope(dir, shape, half, states.getB(), side);
        }
    }

    private static boolean testAgainstDoubleHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(dir, shape, half, states.getA(), side) ||
               testAgainstHalfSlope(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalHalfSlope(dir, shape, half, states.getA(), side) ||
               testAgainstVerticalHalfSlope(dir, shape, half, states.getB(), side);
    }

    private static boolean testAgainstSlopedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return getHalfDir(dir, shape, half, side).isEqualTo(SlopedStairsSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite()));
    }

    private static boolean testAgainstVerticalSlopedStairs(
            Direction dir, StairsShape shape, Half half, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        return getHalfDir(dir, shape, half, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite()));
    }



    public static TriangleDir getStairDir(Direction dir, StairsShape shape, Half half, Direction side)
    {
        Direction dirTwo = half == Half.TOP ? Direction.UP : Direction.DOWN;
        return switch (shape)
        {
            case STRAIGHT ->
            {
                if (side == dir.getClockWise() || side == dir.getCounterClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                yield TriangleDir.NULL;
            }
            case INNER_LEFT ->
            {
                if (side == dir.getOpposite())
                {
                    yield TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                }
                if (side == dir.getClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                if (side == dirTwo)
                {
                    yield TriangleDir.fromDirections(dir, dir.getCounterClockWise());
                }
                yield TriangleDir.NULL;
            }
            case INNER_RIGHT ->
            {
                if (side == dir.getOpposite())
                {
                    yield TriangleDir.fromDirections(dir.getClockWise(), dirTwo);
                }
                if (side == dir.getCounterClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                if (side == dirTwo)
                {
                    yield TriangleDir.fromDirections(dir, dir.getClockWise());
                }
                yield TriangleDir.NULL;
            }
            case OUTER_LEFT ->
            {
                if (side == dir)
                {
                    yield TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                }
                if (side == dir.getCounterClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                yield TriangleDir.NULL;
            }
            case OUTER_RIGHT ->
            {
                if (side == dir)
                {
                    yield TriangleDir.fromDirections(dir.getClockWise(), dirTwo);
                }
                if (side == dir.getClockWise())
                {
                    yield TriangleDir.fromDirections(dir, dirTwo);
                }
                yield TriangleDir.NULL;
            }
        };
    }

    public static HalfDir getHalfDir(Direction dir, StairsShape shape, Half half, Direction side)
    {
        Direction edge = half == Half.TOP ? Direction.UP : Direction.DOWN;
        return switch (shape)
        {
            case INNER_LEFT, INNER_RIGHT -> HalfDir.NULL;
            case STRAIGHT ->
            {
                if (side == dir.getOpposite())
                {
                    yield HalfDir.fromDirections(side, edge);
                }
                yield HalfDir.NULL;
            }
            case OUTER_LEFT ->
            {
                if (side == dir.getOpposite() || side == dir.getClockWise())
                {
                    yield HalfDir.fromDirections(side, edge);
                }
                yield HalfDir.NULL;
            }
            case OUTER_RIGHT ->
            {
                if (side == dir.getOpposite() || side == dir.getCounterClockWise())
                {
                    yield HalfDir.fromDirections(side, edge);
                }
                yield HalfDir.NULL;
            }
        };
    }

    public static CornerDir getCornerDir(Direction dir, StairsShape shape, Half half, Direction side)
    {
        Direction normal = half == Half.TOP ? Direction.DOWN : Direction.UP;
        if (side != normal)
        {
            return CornerDir.NULL;
        }

        return switch (shape)
        {
            case STRAIGHT, INNER_LEFT, INNER_RIGHT -> CornerDir.NULL;
            case OUTER_LEFT -> CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            case OUTER_RIGHT -> CornerDir.fromDirections(side, dir, dir.getClockWise());
        };
    }
}