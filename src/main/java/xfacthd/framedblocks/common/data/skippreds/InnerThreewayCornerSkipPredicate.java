package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class InnerThreewayCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock block)) { return false; }

        BlockType adjBlock = block.getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        if (((IFramedBlock)state.getBlock()).getBlockType() == BlockType.FRAMED_INNER_THREEWAY_CORNER) { dir = dir.rotateY(); } //Correct rotation discrepancy of the threeway corner

        if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            return testAgainstInnerThreewayCorner(world, pos, dir, top, adjBlock, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            return testAgainstThreewayCorner(world, pos, dir, top, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_PRISM_CORNER || adjBlock == BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
        {
            return testAgainstDoubleThreewayCorner(world, pos, dir, top, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            return testAgainstSlope(world, pos, dir, top, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            return testAgainstDoubleSlope(world, pos, dir, top, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            return testAgainstCorner(world, pos, dir, top, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            return testAgainstInnerCorner(world, pos, dir, top, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_CORNER)
        {
            return testAgainstDoubleCorner(world, pos, dir, top, adjState, side);
        }

        return false;
    }

    private boolean testAgainstInnerThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockType adjBlock, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

        if (adjTop == top && ((side == dir.rotateY() && adjDir == dir.rotateY()) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjTop != top && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (adjTop == top && adjDir == dir && (side == dir.rotateY() || side == dir.getOpposite() || (side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(world, pos, side, adjDir);
        }
        return false;
    }

    private boolean testAgainstDoubleThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (adjTop == top && ((side == dir.getOpposite() && adjDir == dir.rotateYCCW()) || (side == dir.rotateY() && adjDir == dir.rotateY())))
        {
            return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
        }
        else if (adjTop != top && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(world, pos, side, top ? Direction.DOWN : Direction.UP);
        }
        else if (adjTop != top && adjDir == dir.getOpposite() && (side == dir.getOpposite() || side == dir.rotateY() ||
                                                                  (side == Direction.UP && !top) || (side == Direction.DOWN && top)
        ))
        {
            return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
        }
        return false;
    }

    private boolean testAgainstSlope(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

        if (adjType != SlopeType.HORIZONTAL && ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.rotateYCCW())))
        {
            return (adjType == SlopeType.TOP) == top && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjType == SlopeType.HORIZONTAL && adjDir == dir && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoubleSlope(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

        if (adjType != SlopeType.HORIZONTAL)
        {
            if ((adjDir == dir && (adjType == SlopeType.TOP) == top) || (adjDir == dir.getOpposite() && (adjType == SlopeType.TOP) != top))
            {
                return side == dir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if ((adjDir == dir.rotateYCCW() && (adjType == SlopeType.TOP) == top) || (adjDir == dir.rotateY() && (adjType == SlopeType.TOP) != top))
            {
                return side == dir.getOpposite() && SideSkipPredicate.compareState(world, pos, side, dir.rotateYCCW());
            }
        }
        else if (adjDir == dir || adjDir == dir.getOpposite())
        {
            return ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && adjType.isTop() == top && adjDir == dir && (side == dir.rotateY() || side == dir.getOpposite()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjType.isHorizontal() && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) &&
                ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.rotateYCCW() && adjType.isRight()))
        )
        {
            return adjType.isTop() == top && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjType.isHorizontal() && ((side == dir.rotateY() && adjDir == dir && !adjType.isRight()) ||
                (side == dir.getOpposite() && adjDir == dir.rotateYCCW() && adjType.isRight()))
        )
        {
            return adjType.isTop() == top && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstInnerCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal() && adjType.isTop() == top && ((side == dir.rotateY() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getOpposite())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjType.isHorizontal() && adjType.isTop() != top && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)) &&
                ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.rotateYCCW() && adjType.isRight()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjType.isHorizontal() && adjType.isTop() == top && ((side == dir.rotateY() && adjDir == dir && adjType.isRight()) ||
                (side == dir.getOpposite() && adjDir == dir.rotateYCCW() && !adjType.isRight()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoubleCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!adjType.isHorizontal())
        {
            if (adjType.isTop() == top && ((side == dir.getOpposite() && adjDir == dir.rotateYCCW()) || (side == dir.rotateY() && adjDir == dir.rotateY())))
            {
                return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
            }
            else if (adjType.isTop() != top && ((side == dir.getOpposite() && adjDir == dir.getOpposite()) || (side == dir.rotateY() && adjDir == dir.getOpposite())))
            {
                return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
            }
        }
        else
        {
            if (adjType.isTop() != top && ((side == Direction.UP && !top) || (side == Direction.DOWN && top)))
            {
                if ((adjDir == dir && !adjType.isRight()) || (adjDir == dir.rotateYCCW() && adjType.isRight()))
                {
                    return SideSkipPredicate.compareState(world, pos, side, adjDir);
                }
                else if ((adjDir == dir.getOpposite() && !adjType.isRight()) || (adjDir == dir.rotateY() && adjType.isRight()))
                {
                    return SideSkipPredicate.compareState(world, pos, side, adjDir.getOpposite());
                }
            }
            else if (adjType.isTop() == top && ((side == dir.getOpposite() && !adjType.isRight() && adjDir == dir.rotateYCCW()) ||
                                                (side == dir.rotateY() && adjType.isRight() && adjDir == dir)
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, adjDir);
            }
            else if (adjType.isTop() != top && ((side == dir.rotateY() && !adjType.isRight() && adjDir == dir.getOpposite()) ||
                                                (side == dir.getOpposite() && adjType.isRight() && adjDir == dir.rotateY())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, adjDir.getOpposite());
            }
        }
        return false;
    }
}