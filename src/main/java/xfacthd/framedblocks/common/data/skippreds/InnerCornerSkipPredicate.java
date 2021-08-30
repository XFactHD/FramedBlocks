package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class InnerCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock block)) { return false; }

        BlockType adjBlock = block.getBlockType();
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            return testAgainstInnerCorner(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            return testAgainstCorner(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_CORNER)
        {
            return testAgainstDoubleCorner(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            return testAgainstSlope(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            return testAgainstDoubleSlope(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            return testAgainstThreewaySlope(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            return testAgainstInnerThreewaySlope(level, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_PRISM_CORNER || adjBlock == BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
        {
            return testAgainstDoubleThreewayCorner(level, pos, dir, type, adjState, side);
        }

        return false;
    }

    private boolean testAgainstInnerCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                                                        (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())
        ))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && type.isHorizontalAdjacentInner(dir, side, adjType) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir.getClockWise() && adjType.isRight() && adjDir == dir) ||
                 (side == dir.getOpposite() && !adjType.isRight() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                 (type.isRight() && side == dir.getCounterClockWise() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && !adjType.isHorizontal() && adjDir == dir && adjType.isTop() == type.isTop())
        {
            return (side == dir.getClockWise() || side == dir.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && adjType == type && adjDir == dir && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) ||
                (side == dir.getClockWise() && !type.isRight()) || (side == dir.getCounterClockWise() && type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir.getClockWise() && !adjType.isRight() && adjDir == dir) ||
                 (side == dir.getOpposite() && adjType.isRight() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.getClockWise() && adjDir == dir) || (type.isRight() && side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoubleCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && !adjType.isHorizontal())
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (type.isTop() != adjType.isTop() && adjDir == dir.getOpposite() && (side == dir.getClockWise() || side == dir.getOpposite()))
            {
                return SideSkipPredicate.compareState(level, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (type.isHorizontal() && !adjType.isHorizontal())
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getCounterClockWise() && adjDir == dir && type.isRight()) ||
                                                    (side == dir.getClockWise() && adjDir == dir.getClockWise() && !type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (type.isTop() != adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir.getOpposite() && !type.isRight()) ||
                                                         (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise() && type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (!type.isHorizontal()/* && adjType.isHorizontal()*/)
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir && adjType.isRight()) ||
                                                    (side == dir.getOpposite() && adjDir == dir.getCounterClockWise() && !adjType.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjDir);
            }
            else if (type.isTop() != adjType.isTop() && ((side == dir.getClockWise() && adjDir == dir.getOpposite() && !adjType.isRight()) ||
                                                         (side == dir.getOpposite() && adjDir == dir.getClockWise() && adjType.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjDir.getOpposite());
            }
        }
        else /*if (type.isHorizontal() && adjType.isHorizontal())*/
        {
            if (adjDir == dir)
            {
                if (type.isRight() == adjType.isRight() && ((side == Direction.UP && !type.isTop() && adjType.isTop()) ||
                                                            (side == Direction.DOWN && type.isTop() && !adjType.isTop())
                ))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir);
                }
                else if (type.isTop() == adjType.isTop() && ((side == dir.getClockWise() && !type.isRight() && adjType.isRight()) ||
                                                             (side == dir.getCounterClockWise() && type.isRight() && !adjType.isRight())
                ))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir);
                }
            }
            else if (adjDir == dir.getOpposite())
            {
                if (type.isRight() == adjType.isRight() && ((side == Direction.UP && !type.isTop() && adjType.isTop()) ||
                                                            (side == Direction.DOWN && type.isTop() && !adjType.isTop())
                ))
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir);
                }
                else if (type.isRight() == adjType.isRight() && type.isTop() != adjType.isTop() &&
                        ((side == dir.getCounterClockWise() && type.isRight()) || (side == dir.getClockWise() && !type.isRight()))
                )
                {
                    return SideSkipPredicate.compareState(level, pos, side, dir);
                }
            }
        }
        return false;
    }

    private boolean testAgainstSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getClockWise() && adjDir == dir) ||
                                                                        (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        )
        {
            return (adjType == SlopeType.TOP) == type.isTop() && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal())
        {
            if (((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())) && adjType == SlopeType.HORIZONTAL)
            {
                return ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.getClockWise())) && SideSkipPredicate.compareState(level, pos, side);
            }
            else if (side.getAxis() != Direction.Axis.Y && adjDir == dir && (adjType == SlopeType.TOP) == type.isTop())
            {
                return ((!type.isRight() && side == dir.getClockWise()) || (type.isRight() && side == dir.getCounterClockWise())) &&
                        SideSkipPredicate.compareState(level, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleSlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjTop = adjType == SlopeType.TOP;

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (
            (side == dir.getClockWise() && type.isTop() == adjTop && adjDir == dir) ||
            (side == dir.getClockWise() && type.isTop() != adjTop && adjDir == dir.getOpposite()) ||
            (side == dir.getOpposite() && type.isTop() == adjTop && adjDir == dir.getCounterClockWise()) ||
            (side == dir.getOpposite() && type.isTop() != adjTop && adjDir == dir.getClockWise())
        ))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, face);
        }
        else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())))
        {
            if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) ||
                (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir);
            }
        }
        else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getClockWise() && !type.isRight()) ||
                                                                            (side == dir.getCounterClockWise() && type.isRight()))
        )
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) &&
                    SideSkipPredicate.compareState(level, pos, side, face);
        }
        return false;
    }

    private boolean testAgainstThreewaySlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (!type.isHorizontal() && adjDir == dir && adjTop == type.isTop() && (side == dir.getClockWise() || side == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal() && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.getClockWise())) && adjTop == type.isTop() &&
                ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) || (side == dir.getClockWise() && !type.isRight()) ||
                        (side == dir.getCounterClockWise() && type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }

    private boolean testAgainstInnerThreewaySlope(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (!type.isHorizontal() && adjTop == type.isTop() && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                                                               (side == dir.getOpposite() && adjDir == dir.getCounterClockWise()))
        )
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        else if (type.isHorizontal())
        {
            if (adjTop != type.isTop() && ((adjDir == dir && !type.isRight()) || (adjDir == dir.getClockWise() && type.isRight())) &&
                ((!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir);
            }
            else if (adjTop == type.isTop() && ((!type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise()) ||
                    (type.isRight() && side == dir.getCounterClockWise() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleThreewayCorner(BlockGetter level, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (!type.isHorizontal())
        {
            if (adjTop == type.isTop() && ((side == dir.getClockWise() && adjDir == dir.getClockWise()) || (side == dir.getOpposite() && adjDir == dir.getCounterClockWise())))
            {
                return SideSkipPredicate.compareState(level, pos, side, adjTop ? Direction.UP : Direction.DOWN);
            }
            else if (adjTop != type.isTop() && adjDir == dir.getOpposite() && (side == dir.getClockWise() || side == dir.getOpposite()))
            {
                return SideSkipPredicate.compareState(level, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else
        {
            if (adjTop == type.isTop() && ((side == dir.getCounterClockWise() && adjDir == dir && type.isRight()) ||
                                           (side == dir.getClockWise() && adjDir == dir.getClockWise() && !type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir);
            }
            else if (adjTop != type.isTop() && ((side == dir.getClockWise() && adjDir == dir.getOpposite() && !type.isRight()) ||
                                                (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise() && type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(level, pos, side, dir);
            }
            else if (adjTop != type.isTop() && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())) &&
                     ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) ||
                      (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise())))
            )
            {
                return SideSkipPredicate.compareState(level, pos, side, dir);
            }
        }
        return false;
    }
}