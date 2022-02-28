package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class CornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            return testAgainstCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_CORNER)
        {
            return testAgainstDoubleCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_SLOPE || adjBlock == BlockType.FRAMED_RAIL_SLOPE)
        {
            return testAgainstSlope(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            return testAgainstDoubleSlope(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            return testAgainstInnerCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            return testAgainstThreewayCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            return testAgainstInnerThreewayCorner(world, pos, dir, type, adjBlock, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_PRISM_CORNER || adjBlock == BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
        {
            return testAgainstDoubleThreewayCorner(world, pos, dir, type, adjState, side);
        }

        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise())))
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && type.isHorizontalAdjacent(dir, side, adjType) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                 ((side == dir && !adjType.isRight() && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjType.isRight() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                 ((!type.isRight() && side == dir.getCounterClockWise() && adjDir == dir.getClockWise()) || (type.isRight() && side == dir.getClockWise() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstDoubleCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && !type.isHorizontal())
        {
            if (adjType.isTop() == type.isTop() && adjDir == dir && (side == dir || side == dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (adjType.isTop() != type.isTop() && ((side == dir && adjDir == dir.getClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise())))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (type.isHorizontal() && !adjType.isHorizontal())
        {
            if ((adjDir == dir && side == dir.getCounterClockWise() && !type.isRight() && adjType.isTop() == type.isTop()) ||
                    (adjDir == dir.getOpposite() && side == dir.getClockWise() && type.isRight() && adjType.isTop() != type.isTop())
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if ((adjDir == dir.getCounterClockWise() && side == dir.getCounterClockWise() && !type.isRight() && adjType.isTop() != type.isTop()) ||
                     (adjDir == dir.getClockWise() && side == dir.getClockWise() && type.isRight() && adjType.isTop() == type.isTop())
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (!type.isHorizontal() /*&& adjType.isHorizontal()*/)
        {
            if ((side == dir.getCounterClockWise() && adjDir == dir && !adjType.isRight()) ||
                    (side == dir && adjDir == dir.getCounterClockWise() && adjType.isRight())
            )
            {
                return adjType.isTop() == type.isTop() && SideSkipPredicate.compareState(world, pos, side, adjDir);
            }
            else if ((side == dir.getCounterClockWise() && adjDir == dir.getOpposite() && adjType.isRight()) ||
                    (side == dir && adjDir == dir.getClockWise() && !adjType.isRight())
            )
            {
                return adjType.isTop() != type.isTop() && SideSkipPredicate.compareState(world, pos, side, adjDir.getOpposite());
            }
        }
        else /*if (type.isHorizontal() && adjType.isHorizontal())*/
        {
            if (adjDir == dir && type == adjType && ((side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight()) ||
                    (side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (adjDir == dir.getOpposite() && type.isTop() != adjType.isTop() && type.isRight() != adjType.isRight() &&
                     ((side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (adjDir == dir.getOpposite() && adjType == type && ((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstSlope(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = Utils.getBlockFacing(adjState);
        SlopeType adjType = Utils.getSlopeType(adjState);

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (adjType == SlopeType.TOP) == type.isTop())
        {
            if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir))
            {
                Direction face = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(world, pos, side, face);
            }
        }
        else if (type.isHorizontal())
        {
            if (((side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight())) && (adjType == SlopeType.TOP) == type.isTop())
            {
                return adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
            }
            else if ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()))
            {
                return ((type.isRight() && adjDir == dir.getClockWise()) || (!type.isRight() && adjDir == dir)) && SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleSlope(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        boolean adjTop = adjType == SlopeType.TOP;

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (
                (side == dir && type.isTop() == adjTop && adjDir == dir.getCounterClockWise()) ||
                        (side == dir && type.isTop() != adjTop && adjDir == dir.getClockWise()) ||
                        (side == dir.getCounterClockWise() && type.isTop() == adjTop && adjDir == dir) ||
                        (side == dir.getCounterClockWise() && type.isTop() != adjTop && adjDir == dir.getOpposite())
        ))
        {
            Direction face = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(world, pos, side, face);
        }
        else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())))
        {
            if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise())))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getCounterClockWise() && !type.isRight()) || (side == dir.getClockWise() && type.isRight())))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) && SideSkipPredicate.compareState(world, pos, side, face);
        }
        return false;
    }

    private boolean testAgainstInnerCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && adjDir == dir.getCounterClockWise() && (side == dir || side == dir.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && adjType == type && ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()) ||
                (side == dir.getClockWise() && type.isRight()) || (side == dir.getCounterClockWise() && !type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir && adjType.isRight() && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && !adjType.isRight() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()) || (type.isRight() && side == dir.getClockWise() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (!type.isHorizontal() && type.isTop() == adjTop)
        {
            if ((side == dir && adjDir == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && adjDir == dir.getClockWise()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (type.isHorizontal())
        {
            if ((side == dir.getClockWise() && type.isRight() && adjDir == dir && type.isTop() == adjTop) ||
                (side == dir.getCounterClockWise() && !type.isRight() && adjDir == dir.getClockWise() && type.isTop() == adjTop)
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (side.getAxis() == Direction.Axis.Y && type.isTop() != adjTop && (side == Direction.DOWN) == !type.isTop() &&
                    ((type.isRight() && adjDir == dir.getClockWise()) || (!type.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstInnerThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockType adjBlock, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.getClockWise(); } //Correct rotation discrepancy of the threeway corner

        if (!type.isHorizontal() && type.isTop() == adjTop && adjDir == dir)
        {
            return (side == dir || side == dir.getCounterClockWise()) && SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal())
        {
            if (side.getAxis() == Direction.Axis.Y && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.getClockWise())))
            {
                return type.isTop() == adjTop && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if ((!type.isRight() && side == dir.getCounterClockWise() && adjDir == dir) ||
                     (type.isRight() && side == dir.getClockWise() && adjDir == dir.getClockWise())
            )
            {
                return type.isTop() == adjTop && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.getValue(PropertyHolder.TOP);

        if (!type.isHorizontal())
        {
            if (adjDir == dir && adjTop == type.isTop() && (side == dir || side == dir.getCounterClockWise()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (adjTop != type.isTop() && ((side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise()) || (side == dir && adjDir == dir.getClockWise())))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (adjTop == type.isTop())
        {
            if ((side == dir.getCounterClockWise() && adjDir == dir && !type.isRight()) || (side == dir.getClockWise() && adjDir == dir.getClockWise() && type.isRight()))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())) &&
                     ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) ||
                      (type.isRight() && (adjDir == dir.getClockWise() || adjDir == dir.getCounterClockWise()))
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if ((side == dir.getCounterClockWise() && adjDir == dir.getCounterClockWise() && !type.isRight()) || (side == dir.getClockWise() && adjDir == dir.getOpposite() && type.isRight()))
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }
}