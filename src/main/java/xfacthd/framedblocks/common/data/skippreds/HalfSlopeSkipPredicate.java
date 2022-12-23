package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class HalfSlopeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            return switch (blockType)
            {
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> testAgainstSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        level, pos, state, dir, right, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        level, pos, state, dir, right, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        level, pos, state, dir, right, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side == dir && adjDir == dir.getOpposite() && adjRight != right)
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (isVerticalPanelFace(top, side) && isSameHorizontalHalf(dir, right, adjDir, adjRight) && adjTop != top)
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (adjDir == dir && adjRight != right && isOuterTriangle(dir, right, side))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if ((side == dir || isVerticalPanelFace(top, side)) && isSameHorizontalHalf(dir, right, adjDir, adjRight))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (isOuterTriangle(dir, right, side) && ((adjDir == dir && !top && adjRight != right) || (adjDir == dir.getOpposite() && top && adjRight == right)))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, dir);
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjType == SlopeType.TOP;

        if (adjDir == dir && adjTop == top && isOuterTriangle(dir, right, side))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (side == dir && adjDir == dir.getOpposite())
        {
            return SideSkipPredicate.compareState(level, pos, side, state, getOuterSlopeFace(dir, right));
        }
        else if (adjTop != top && adjDir.getAxis() == dir.getAxis() && isVerticalPanelFace(top, side))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, getOuterSlopeFace(dir, right));
        }
        return false;
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if ((adjRot == HorizontalRotation.RIGHT || adjRot == HorizontalRotation.DOWN) != right)
        {
            return false;
        }
        if ((adjRot == HorizontalRotation.DOWN || adjRot == HorizontalRotation.LEFT) != top)
        {
            return false;
        }

        if ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = FramedUtils.getSlopeType(adjState);
        if (adjType == SlopeType.HORIZONTAL || !isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = FramedUtils.getSlopeBlockFacing(adjState);
        boolean adjTop = adjType == SlopeType.TOP;

        if (adjTop == top && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL || !isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjType == SlopeType.TOP;

        if ((adjTop == top && adjDir == dir) || (adjTop != top && adjDir == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, top ? Direction.UP : Direction.DOWN);
        }

        return false;
    }

    private static boolean testAgainstCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            if (adjDir == dir && adjType.isTop() == top && adjType.isRight() != right)
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }
        else
        {
            boolean adjTop = adjType == CornerType.TOP;

            if (adjTop == top && ((!right && adjDir == dir.getClockWise()) || (right && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstInnerCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            if (adjDir == dir && adjType.isTop() == top && adjType.isRight() == right)
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }
        else
        {
            boolean adjTop = adjType == CornerType.TOP;

            if (adjTop == top && ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            boolean adjTop = adjType.isTop();
            boolean adjRight = adjType.isRight();

            if (adjDir == dir && adjTop == top && adjRight == right)
            {
                return SideSkipPredicate.compareState(level, pos, side, state, dir);
            }
            else if (adjDir == dir.getOpposite() && adjTop != top && adjRight != right)
            {
                return SideSkipPredicate.compareState(level, pos, side, state, dir);
            }
        }
        else
        {
            boolean adjTop = adjType == CornerType.TOP;

            if (adjTop == top && ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, top ? Direction.UP : Direction.DOWN);
            }
            else if (adjTop != top && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getOpposite())))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, top ? Direction.UP : Direction.DOWN);
            }
        }

        return false;
    }

    private static boolean testAgainstThreewayCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && ((!right && adjDir == dir.getClockWise()) || (right && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleThreewayCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isOuterTriangle(dir, right, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, top ? Direction.UP : Direction.DOWN);
        }
        else if (adjTop != top && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, top ? Direction.UP : Direction.DOWN);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isVerticalPanelFace(top, side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return adjTop != top && SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstPanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isVerticalPanelFace(top, side) && side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        if (!isVerticalPanelFace(top, side) && side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);

        return adjDir.getAxis() != dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, state, getOuterSlopeFace(dir, right));
    }

    private static boolean testAgainstCornerPillar(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean right, BlockState adjState, Direction side
    )
    {
        if (side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        if (adjShape != StairsShape.STRAIGHT || !isVerticalPanelFace(top, side)) { return false; }

        Direction adjDir = adjState.getValue(StairBlock.FACING);
        boolean adjTop = adjState.getValue(StairBlock.HALF) == Half.TOP;

        if ((right && adjDir == dir.getClockWise()) || (!right && adjDir == dir.getCounterClockWise()))
        {
            return adjTop == top && SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop == top && adjDir.getAxis() == dir.getClockWise().getAxis() && isVerticalPanelFace(top, side))
        {
            Direction camoFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            return SideSkipPredicate.compareState(level, pos, side, state, camoFace);
        }

        return false;
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean right, BlockState adjState, Direction side
    )
    {
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);
        if (adjType != StairsType.VERTICAL || side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if ((!right && adjDir == dir) || (right && adjDir == dir.getClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean right, BlockState adjState, Direction side
    )
    {
        if (side != dir) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (adjDir == dir || adjDir == dir.getClockWise())
        {
            Direction camoFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            return SideSkipPredicate.compareState(level, pos, side, state, camoFace);
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (side == dir && adjDir == dir.getOpposite() && adjRight != right)
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (isVerticalPanelFace(top, side) && adjTop != top)
        {
            if ((adjDir == dir && adjRight == right) || (adjDir == dir.getOpposite() && adjRight != right))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side != adjRot.withFacing(adjDir) || (adjRot.isVertical() && top != (adjRot == HorizontalRotation.UP))) { return false; }

        if (!right && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (right && ((adjDir == dir.getClockWise() && !adjFront) || (adjDir == dir.getCounterClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.withFacing(adjDir) != side.getOpposite()) { return false; }

        if (adjRot.isVertical() && top == (adjRot == HorizontalRotation.DOWN) && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (!adjRot.isVertical() && ((!right && adjDir == dir.getCounterClockWise()) || (right && adjDir == dir.getClockWise())))
        {
            return right == (adjRot == HorizontalRotation.RIGHT) && SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (side.getAxis() != adjRot.withFacing(adjDir).getAxis() || (adjRot.isVertical() && top != (side == Direction.UP)))
        {
            return false;
        }

        if (!right && ((adjDir == dir.getCounterClockWise() && !adjFront) || (adjDir == dir.getClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }
        else if (right && ((adjDir == dir.getClockWise() && !adjFront) || (adjDir == dir.getCounterClockWise() && adjFront)))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side.getAxis() != adjRot.withFacing(adjDir).getAxis() || (adjRot.isVertical() && top != (side == Direction.UP)))
        {
            return false;
        }

        if (adjRot.isVertical())
        {
            boolean sameOrientation = top == (adjRot == HorizontalRotation.UP);
            if (!right && ((adjDir == dir.getCounterClockWise() && !sameOrientation) || (adjDir == dir.getClockWise() && sameOrientation)))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
            else if (right && ((adjDir == dir.getClockWise() && !sameOrientation) || (adjDir == dir.getCounterClockWise() && sameOrientation)))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }
        else
        {
            if (right == (adjRot == HorizontalRotation.RIGHT) && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjRot.withFacing(adjDir) != side.getOpposite()) { return false; }

        if ((!adjRot.isVertical() || top == (adjRot == HorizontalRotation.DOWN)) && adjDir.getAxis() == dir.getClockWise().getAxis())
        {
            Direction camoDir = right ? dir.getClockWise() : dir.getCounterClockWise();
            return SideSkipPredicate.compareState(level, pos, side, state, camoDir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            if ((adjDir == dir.getCounterClockWise() && right == adjFront) || (adjDir == dir.getClockWise() && right != adjFront))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        Direction checkDir = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()) && adjDir == checkDir)
        {
            if (side == dir || (!top && side == Direction.DOWN) || (top && side == Direction.UP))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, adjDir);
            }
        }

        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            if ((adjDir == dir.getCounterClockWise() && right == adjFront) || (adjDir == dir.getClockWise() && right != adjFront))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (!FlatInnerSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite())) { return false; }

        if (side == dir || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            if ((adjDir == dir.getCounterClockWise() && !right) || (adjDir == dir.getClockWise() && right))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        Direction checkDir = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (FlatExtendedSlopePanelCornerSkipPredicate.isPanelSide(adjDir, adjRot, side.getOpposite()) && adjDir.getAxis() == checkDir.getAxis())
        {
            if (side == dir || (!top && side == Direction.DOWN) || (top && side == Direction.UP))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, checkDir);
            }
        }

        return false;
    }



    public static boolean isOuterTriangle(Direction dir, boolean right, Direction side)
    {
        return (!right && side == dir.getCounterClockWise()) || (right && side == dir.getClockWise());
    }

    public static boolean isPanelFace(Direction dir, boolean top, Direction side)
    {
        return side == dir || isVerticalPanelFace(top, side);
    }

    public static boolean isVerticalPanelFace(boolean top, Direction side)
    {
        return (!top && side == Direction.DOWN) || (top && side == Direction.UP);
    }

    public static boolean isSameHorizontalHalf(Direction dir, boolean right, Direction adjDir, boolean adjRight)
    {
        return (adjDir == dir && adjRight == right) || (adjDir == dir.getOpposite() && adjRight != right);
    }

    public static Direction getOuterSlopeFace(Direction dir, boolean right)
    {
        return right ? dir.getClockWise() : dir.getCounterClockWise();
    }
}
