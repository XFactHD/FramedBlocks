package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class CornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

        if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            return testAgainstCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_CORNER)
        {
            return testAgainstDoubleCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_SLOPE)
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

        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY())))
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && type.isHorizontalAdjacent(dir, side, adjType) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                 ((side == dir && !adjType.isRight() && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjType.isRight() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                 ((!type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateY()) || (type.isRight() && side == dir.rotateY() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstDoubleCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && !type.isHorizontal())
        {
            if (adjType.isTop() == type.isTop() && adjDir == dir && (side == dir || side == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (adjType.isTop() != type.isTop() && ((side == dir && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && adjDir == dir.rotateYCCW())))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (type.isHorizontal() && !adjType.isHorizontal())
        {
            if ((adjDir == dir && side == dir.rotateYCCW() && !type.isRight() && adjType.isTop() == type.isTop()) ||
                    (adjDir == dir.getOpposite() && side == dir.rotateY() && type.isRight() && adjType.isTop() != type.isTop())
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if ((adjDir == dir.rotateYCCW() && side == dir.rotateYCCW() && !type.isRight() && adjType.isTop() != type.isTop()) ||
                     (adjDir == dir.rotateY() && side == dir.rotateY() && type.isRight() && adjType.isTop() == type.isTop())
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (!type.isHorizontal() /*&& adjType.isHorizontal()*/)
        {
            if ((side == dir.rotateYCCW() && adjDir == dir && !adjType.isRight()) ||
                    (side == dir && adjDir == dir.rotateYCCW() && adjType.isRight())
            )
            {
                return adjType.isTop() == type.isTop() && SideSkipPredicate.compareState(world, pos, side, adjDir);
            }
            else if ((side == dir.rotateYCCW() && adjDir == dir.getOpposite() && adjType.isRight()) ||
                    (side == dir && adjDir == dir.rotateY() && !adjType.isRight())
            )
            {
                return adjType.isTop() != type.isTop() && SideSkipPredicate.compareState(world, pos, side, adjDir.getOpposite());
            }
        }
        else /*if (type.isHorizontal() && adjType.isHorizontal())*/
        {
            if (adjDir == dir && type == adjType && ((side == dir.rotateY() && type.isRight()) || (side == dir.rotateYCCW() && !type.isRight()) ||
                    (side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (adjDir == dir.getOpposite() && type.isTop() != adjType.isTop() && type.isRight() != adjType.isRight() &&
                     ((side == dir.rotateY() && type.isRight()) || (side == dir.rotateYCCW() && !type.isRight()))
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
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (adjType == SlopeType.TOP) == type.isTop())
        {
            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir))
            {
                Direction face = type.isTop() ? Direction.UP : Direction.DOWN;
                return SideSkipPredicate.compareState(world, pos, side, face);
            }
        }
        else if (type.isHorizontal())
        {
            if (((side == dir.rotateY() && type.isRight()) || (side == dir.rotateYCCW() && !type.isRight())) && (adjType == SlopeType.TOP) == type.isTop())
            {
                return adjDir == dir && SideSkipPredicate.compareState(world, pos, side);
            }
            else if ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()))
            {
                return ((type.isRight() && adjDir == dir.rotateY()) || (!type.isRight() && adjDir == dir)) && SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleSlope(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);
        boolean adjTop = adjType == SlopeType.TOP;

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && (
                (side == dir && type.isTop() == adjTop && adjDir == dir.rotateYCCW()) ||
                        (side == dir && type.isTop() != adjTop && adjDir == dir.rotateY()) ||
                        (side == dir.rotateYCCW() && type.isTop() == adjTop && adjDir == dir) ||
                        (side == dir.rotateYCCW() && type.isTop() != adjTop && adjDir == dir.getOpposite())
        ))
        {
            Direction face = type.isTop() ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(world, pos, side, face);
        }
        else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop())))
        {
            if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW())))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.rotateYCCW() && !type.isRight()) || (side == dir.rotateY() && type.isRight())))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) && SideSkipPredicate.compareState(world, pos, side, face);
        }
        return false;
    }

    private boolean testAgainstInnerCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && adjDir == dir.rotateYCCW() && (side == dir || side == dir.rotateYCCW()))
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && adjType == type && ((side == Direction.UP && type.isTop()) || (side == Direction.DOWN && !type.isTop()) ||
                (side == dir.rotateY() && type.isRight()) || (side == dir.rotateYCCW() && !type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir && adjType.isRight() && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && !adjType.isRight() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateYCCW()) || (type.isRight() && side == dir.rotateY() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (!type.isHorizontal() && type.isTop() == adjTop)
        {
            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (type.isHorizontal())
        {
            if ((side == dir.rotateY() && type.isRight() && adjDir == dir && type.isTop() == adjTop) ||
                (side == dir.rotateYCCW() && !type.isRight() && adjDir == dir.rotateY() && type.isTop() == adjTop)
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (side.getAxis() == Direction.Axis.Y && type.isTop() != adjTop && (side == Direction.DOWN) == !type.isTop() &&
                    ((type.isRight() && adjDir == dir.rotateY()) || (!type.isRight() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstInnerThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockType adjBlock, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

        if (!type.isHorizontal() && type.isTop() == adjTop && adjDir == dir)
        {
            return (side == dir || side == dir.rotateYCCW()) && SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
        }
        else if (type.isHorizontal())
        {
            if (side.getAxis() == Direction.Axis.Y && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.rotateY())))
            {
                return type.isTop() == adjTop && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if ((!type.isRight() && side == dir.rotateYCCW() && adjDir == dir) ||
                     (type.isRight() && side == dir.rotateY() && adjDir == dir.rotateY())
            )
            {
                return type.isTop() == adjTop && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }
}