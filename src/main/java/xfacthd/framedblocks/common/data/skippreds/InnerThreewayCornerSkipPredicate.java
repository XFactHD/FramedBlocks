package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.FramedUtils;

public class InnerThreewayCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            boolean top = state.getValue(PropertyHolder.TOP);

            return switch (type)
            {
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(level, pos, dir, top, adjState, side);
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(level, pos, dir, top, adjState, side);
                case FRAMED_SLOPE, FRAMED_RAIL_SLOPE -> testAgainstSlope(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(level, pos, dir, top, adjState, side);
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(level, pos, dir, top, adjState, side);
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(level, pos, dir, top, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == top && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjTop != top && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == top && adjDir == dir && (side == dir.getClockWise() || side == dir.getOpposite() ||
                                               (side == Direction.UP && !top) || (side == Direction.DOWN && top))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side, adjDir);
        }
        return false;
    }

    private static boolean testAgainstDoubleThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjTop == top && ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) || (side == dir.getClockWise() && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
        }
        else if (adjTop != top && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(level, pos, side, top ? Direction.DOWN : Direction.UP);
        }
        else if (adjTop != top && adjDir == dir.getOpposite() && (side == dir.getOpposite() || side == dir.getClockWise() ||
                                                                  (side == Direction.UP && !top) || (side == Direction.DOWN && top)
        ))
        {
            return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
        }
        return false;
    }

    private static boolean testAgainstSlope(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = FramedUtils.getBlockFacing(adjState);
        SlopeType adjType = FramedUtils.getSlopeType(adjState);

        if (adjType != SlopeType.HORIZONTAL && ((side == dir.getClockWise() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
        {
            return (adjType == SlopeType.TOP) == top && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjType == SlopeType.HORIZONTAL && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlope(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        if (adjType != SlopeType.HORIZONTAL)
        {
            if ((adjDir == dir && (adjType == SlopeType.TOP) == top) || (adjDir == dir.getOpposite() && (adjType == SlopeType.TOP) != top))
            {
                return side == dir.getClockWise() && SideSkipPredicate.compareState(level, pos, side, dir);
            }
            else if ((adjDir == dir.getCounterClockWise() && (adjType == SlopeType.TOP) == top) || (adjDir == dir.getClockWise() && (adjType == SlopeType.TOP) != top))
            {
                return side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side, dir.getCounterClockWise());
            }
        }
        else if (adjDir == dir || adjDir == dir.getOpposite())
        {
            return ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) && SideSkipPredicate.compareState(level, pos, side, dir);
        }
        return false;
    }

    private static boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && adjType.isTop() == top && adjDir == dir && (side == dir.getClockWise() || side == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjType.isHorizontal() && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) &&
                ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.getCounterClockWise() && adjType.isRight()))
        )
        {
            return adjType.isTop() == top && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjType.isHorizontal() && ((side == dir.getClockWise() && adjDir == dir && !adjType.isRight()) ||
                (side == dir.getOpposite() && adjDir == dir.getCounterClockWise() && adjType.isRight()))
        )
        {
            return adjType.isTop() == top && SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstInnerCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && adjType.isTop() == top && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                                                                  (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjType.isHorizontal() && adjType.isTop() != top && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) &&
                ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.getCounterClockWise() && adjType.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjType.isHorizontal() && adjType.isTop() == top && ((side == dir.getClockWise() && adjDir == dir && adjType.isRight()) ||
                (side == dir.getOpposite() && adjDir == dir.getCounterClockWise() && !adjType.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private static boolean testAgainstDoubleCorner(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal())
        {
            if (adjType.isTop() == top && ((side == dir.getOpposite() && adjDir == dir.getCounterClockWise()) ||
                                           (side == dir.getClockWise() && adjDir == dir.getClockWise()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
            }
            else if (adjType.isTop() != top && ((side == dir.getOpposite() && adjDir == dir.getOpposite()) ||
                                                (side == dir.getClockWise() && adjDir == dir.getOpposite()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
            }
        }
        else
        {
            if (adjType.isTop() != top && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
            {
                if ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.getCounterClockWise() && adjType.isRight()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, adjDir);
                }
                else if ((adjDir == dir.getOpposite() && !adjType.isRight()) || (adjDir == dir.getClockWise() && adjType.isRight()))
                {
                    return SideSkipPredicate.compareState(level, pos, side, adjDir.getOpposite());
                }
            }
            else if (adjType.isTop() == top && ((side == dir.getOpposite() && !adjType.isRight() && adjDir == dir.getCounterClockWise()) ||
                                                (side == dir.getClockWise() && adjType.isRight() && adjDir == dir)
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjDir);
            }
            else if (adjType.isTop() != top && ((side == dir.getClockWise() && !adjType.isRight() && adjDir == dir.getOpposite()) ||
                                                (side == dir.getOpposite() && adjType.isRight() && adjDir == dir.getClockWise())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjDir.getOpposite());
            }
        }
        return false;
    }
}