package xfacthd.framedblocks.common.data.skippreds.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class SlopeSkipPredicate implements SideSkipPredicate
{
    public static final SlopeSkipPredicate INSTANCE = new SlopeSkipPredicate();

    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = FramedUtils.getSlopeBlockFacing(state);
            SlopeType type = FramedUtils.getSlopeType(state);

            return switch (blockType)
            {
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> testAgainstSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(level, pos, dir, type, adjState, side);
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(level, pos, dir, type, adjState, side);
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(level, pos, dir, type, adjState, side);
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(level, pos, dir, type, adjState, side);
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(level, pos, dir, type, adjState, side);
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(level, pos, dir, type, adjState, side);
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_HALF_SLOPE -> testAgainstVerticalHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> testAgainstVerticalDoubleHalfSlope(level, pos, dir, type, adjState, side);
                case FRAMED_SLOPED_STAIRS -> testAgainstSlopedStairs(level, pos, dir, type, adjState, side);
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(level, pos, dir, type, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = FramedUtils.getSlopeBlockFacing(adjState);
        SlopeType adjType = FramedUtils.getSlopeType(adjState);

        if (type == SlopeType.HORIZONTAL && Utils.isY(side))
        {
            return dir == adjDir && type == adjType && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (type != SlopeType.HORIZONTAL && (side == dir.getClockWise() || side == dir.getCounterClockWise()))
        {
            return dir == adjDir && type == adjType && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        if (type == SlopeType.HORIZONTAL && adjType == SlopeType.HORIZONTAL && Utils.isY(side))
        {
            return (dir == adjDir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (type != SlopeType.HORIZONTAL && adjType != SlopeType.HORIZONTAL && (side == dir.getClockWise() || side == dir.getCounterClockWise()))
        {
            return (dir == adjDir && type == adjType) || (dir.getOpposite() == adjDir && type != adjType) && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (side == dir.getClockWise() && adjDir == dir)
        {
            if (type == SlopeType.BOTTOM && (adjType == CornerType.BOTTOM || adjType == CornerType.HORIZONTAL_BOTTOM_LEFT))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (type == SlopeType.TOP && (adjType == CornerType.TOP || adjType == CornerType.HORIZONTAL_TOP_LEFT))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (side == dir.getCounterClockWise())
        {
            if (adjDir == dir)
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.HORIZONTAL_BOTTOM_RIGHT)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.HORIZONTAL_TOP_RIGHT)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
            else if (adjDir == dir.getClockWise())
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.BOTTOM)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.TOP)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
        }
        else if (Utils.isY(side) && type == SlopeType.HORIZONTAL && ((side == Direction.UP) != (adjType.isTop())))
        {
            if (adjType.isRight())
            {
                return dir == adjDir.getClockWise() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else
            {
                return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        return false;
    }

    private static boolean testAgainstDoubleCorner(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            if (type == SlopeType.HORIZONTAL && ((side == Direction.DOWN && !adjType.isTop()) || (side == Direction.UP && adjType.isTop())))
            {
                if ((adjDir == dir || adjDir == dir.getOpposite()) && !adjType.isRight())
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if ((adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()) && adjType.isRight())
                {
                    Direction camoSide = adjDir == dir.getClockWise() ? adjDir.getOpposite() : dir;
                    return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide);
                }
            }
            else if (type != SlopeType.HORIZONTAL && adjDir == dir && ((side == dir.getCounterClockWise() && !adjType.isRight()) ||
                                                                       (side == dir.getClockWise() && adjType.isRight()))
            )
            {
                return (type == SlopeType.TOP) == adjType.isTop() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (type != SlopeType.HORIZONTAL && ((side == dir.getClockWise() && !adjType.isRight()) || (side == dir.getCounterClockWise() && adjType.isRight())))
            {
                return adjDir == dir.getOpposite() && (type == SlopeType.TOP) != adjType.isTop() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else
        {
            if ((side == dir.getCounterClockWise() && adjDir == dir) || (side == dir.getClockWise() && adjDir == dir.getClockWise()))
            {
                return (type == SlopeType.TOP) == adjType.isTop() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if ((side == dir.getClockWise() && adjDir == dir.getOpposite()) || (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()))
            {
                Direction face = adjType.isTop() ? Direction.DOWN : Direction.UP;
                return (type == SlopeType.TOP) != adjType.isTop() && SideSkipPredicate.compareState(level, pos, side, face, face);
            }
        }
        return false;
    }

    private static boolean testAgainstDoubleThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (type != SlopeType.HORIZONTAL)
        {
            if ((type == SlopeType.TOP) == adjTop && ((side == dir.getCounterClockWise() && adjDir == dir) ||
                                                      (side == dir.getClockWise() && adjDir == dir.getClockWise()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if ((type == SlopeType.TOP) != adjTop && ((side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()) ||
                                                           (side == dir.getClockWise() && adjDir == dir.getOpposite()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if ((adjDir == dir || adjDir == dir.getOpposite()) && ((side == Direction.DOWN && !adjTop) || (side == Direction.UP && adjTop)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstInnerCorner(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (side == dir.getClockWise())
        {
            if (adjDir == dir)
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.HORIZONTAL_BOTTOM_RIGHT)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.HORIZONTAL_TOP_RIGHT)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
            else if (adjDir == dir.getClockWise())
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.BOTTOM)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.TOP)
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir, dir);
                }
            }
        }
        else if (side == dir.getCounterClockWise() && adjDir == dir)
        {
            if (type == SlopeType.BOTTOM && (adjType == CornerType.BOTTOM || adjType == CornerType.HORIZONTAL_BOTTOM_LEFT))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (type == SlopeType.TOP && (adjType == CornerType.TOP || adjType == CornerType.HORIZONTAL_TOP_LEFT))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (Utils.isY(side) && type == SlopeType.HORIZONTAL && ((side == Direction.UP) == (adjType.isTop())))
        {
            if (adjType.isRight())
            {
                return dir == adjDir.getClockWise() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else
            {
                return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        return false;
    }

    private static boolean testAgainstThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (type != SlopeType.HORIZONTAL && adjTop == (type == SlopeType.TOP))
        {
            if (side == dir.getClockWise())
            {
                return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (side == dir.getCounterClockWise())
            {
                return adjDir == dir.getClockWise() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (type == SlopeType.HORIZONTAL && Utils.isY(side) && adjTop == (side == Direction.DOWN))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (type != SlopeType.HORIZONTAL && adjTop == (type == SlopeType.TOP))
        {
            if (side == dir.getClockWise())
            {
                return adjDir == dir.getClockWise() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
            else if (side == dir.getCounterClockWise())
            {
                return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
            }
        }
        else if (type == SlopeType.HORIZONTAL && Utils.isY(side) && adjTop == (side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        return false;
    }

    private static boolean testAgainstHalfSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        if (type == SlopeType.HORIZONTAL) { return false; }

        boolean top = type == SlopeType.TOP;

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (adjTop == top && adjDir == dir && ((side == dir.getClockWise() && !adjRight) || (side == dir.getCounterClockWise() && adjRight)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDividedSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        if (adjDir == dir && adjType == type)
        {
            if (type == SlopeType.HORIZONTAL && Utils.isY(side))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
            else if (type != SlopeType.HORIZONTAL && (side == dir.getClockWise() || side == dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
            }
        }

        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        if (type == SlopeType.HORIZONTAL) { return false; }

        boolean top = type == SlopeType.TOP;

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (!top && adjDir == dir && ((!adjRight && side == dir.getClockWise()) || (adjRight && side == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, Direction.DOWN);
        }
        else if (top && adjDir == dir.getOpposite() && ((!adjRight && side == dir.getCounterClockWise()) || (adjRight && side == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, Direction.UP);
        }

        return false;
    }

    private static boolean testAgainstVerticalHalfSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        if (type != SlopeType.HORIZONTAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((!adjTop && side == Direction.UP) || (adjTop && side == Direction.DOWN))
        {
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalDoubleHalfSlope(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        if (type != SlopeType.HORIZONTAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if ((!adjTop && side == Direction.UP) || (adjTop && side == Direction.DOWN))
        {
            return adjDir.getAxis() == dir.getAxis() && SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstSlopedStairs(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        if (type != SlopeType.HORIZONTAL || !Utils.isY(side)) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjDir == dir && ((!adjTop && side == Direction.DOWN) || (adjTop && side == Direction.UP)))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstVerticalSlopedStairs(BlockGetter level, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        if (type == SlopeType.HORIZONTAL) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != side) { return false; }

        boolean top = type == SlopeType.TOP;
        boolean vert = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, top ? Direction.DOWN : Direction.UP);
        boolean hor = VerticalSlopedStairsSkipPredicate.isPanelFace(adjDir, adjRot, dir.getOpposite());
        return vert && hor && SideSkipPredicate.compareState(level, pos, side, dir, side.getOpposite());
    }
}