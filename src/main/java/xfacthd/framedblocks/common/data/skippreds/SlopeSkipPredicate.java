package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class SlopeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);

        if (adjBlock == BlockType.FRAMED_SLOPE)
        {
            return testAgainstSlope(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_SLOPE)
        {
            return testAgainstDoubleSlope(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
        {
            return testAgainstCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_CORNER)
        {
            return testAgainstDoubleCorner(world, pos, dir, type, adjState, side);
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

    private boolean testAgainstSlope(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

        if (type == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y)
        {
            return dir == adjDir && type == adjType && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        else if (type != SlopeType.HORIZONTAL && (side == dir.rotateY() || side == dir.rotateYCCW()))
        {
            return dir == adjDir && type == adjType && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstDoubleSlope(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

        if (type == SlopeType.HORIZONTAL && adjType == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y)
        {
            return (dir == adjDir || adjDir == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        else if (type != SlopeType.HORIZONTAL && adjType != SlopeType.HORIZONTAL && (side == dir.rotateY() || side == dir.rotateYCCW()))
        {
            return (dir == adjDir && type == adjType) || (dir.getOpposite() == adjDir && type != adjType) && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (side == dir.rotateY() && adjDir == dir)
        {
            if (type == SlopeType.BOTTOM && (adjType == CornerType.BOTTOM || adjType == CornerType.HORIZONTAL_BOTTOM_LEFT))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (type == SlopeType.TOP && (adjType == CornerType.TOP || adjType == CornerType.HORIZONTAL_TOP_LEFT))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (side == dir.rotateYCCW())
        {
            if (adjDir == dir)
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.HORIZONTAL_BOTTOM_RIGHT)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.HORIZONTAL_TOP_RIGHT)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (adjDir == dir.rotateY())
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.BOTTOM)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.TOP)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
        }
        else if (side.getAxis() == Direction.Axis.Y && type == SlopeType.HORIZONTAL && ((side == Direction.UP) != (adjType.isTop())))
        {
            if (adjType.isRight())
            {
                return dir == adjDir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleCorner(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (adjType.isHorizontal())
        {
            if (type == SlopeType.HORIZONTAL && ((side == Direction.DOWN && !adjType.isTop()) || (side == Direction.UP && adjType.isTop())))
            {
                if ((adjDir == dir || adjDir == dir.getOpposite()) && !adjType.isRight())
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if ((adjDir == dir.rotateY() || adjDir == dir.rotateYCCW()) && adjType.isRight())
                {
                    return SideSkipPredicate.compareState(world, pos, side, adjDir == dir.rotateY() ? adjDir.getOpposite() : dir);
                }
            }
            else if (type != SlopeType.HORIZONTAL && adjDir == dir && ((side == dir.rotateYCCW() && !adjType.isRight()) || (side == dir.rotateY() && adjType.isRight())))
            {
                return (type == SlopeType.TOP) == adjType.isTop() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (type != SlopeType.HORIZONTAL && ((side == dir.rotateY() && !adjType.isRight()) || (side == dir.rotateYCCW() && adjType.isRight())))
            {
                return adjDir == dir.getOpposite() && (type == SlopeType.TOP) != adjType.isTop() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else
        {
            if ((side == dir.rotateYCCW() && adjDir == dir) || (side == dir.rotateY() && adjDir == dir.rotateY()))
            {
                return (type == SlopeType.TOP) == adjType.isTop() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if ((side == dir.rotateY() && adjDir == dir.getOpposite()) || (side == dir.rotateYCCW() && adjDir == dir.rotateYCCW()))
            {
                Direction face = adjType.isTop() ? Direction.DOWN : Direction.UP;
                return (type == SlopeType.TOP) != adjType.isTop() && SideSkipPredicate.compareState(world, pos, side, face);
            }
        }
        return false;
    }

    private boolean testAgainstInnerCorner(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (side == dir.rotateY() && adjDir == dir)
        {
            if (type == SlopeType.BOTTOM && (adjType == CornerType.BOTTOM || adjType == CornerType.HORIZONTAL_BOTTOM_RIGHT))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (type == SlopeType.TOP && (adjType == CornerType.TOP || adjType == CornerType.HORIZONTAL_TOP_RIGHT))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (side == dir.rotateYCCW())
        {
            if (adjDir == dir)
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.HORIZONTAL_BOTTOM_LEFT)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.HORIZONTAL_TOP_LEFT)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (adjDir == dir.rotateYCCW())
            {
                if (type == SlopeType.BOTTOM && adjType == CornerType.BOTTOM)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type == SlopeType.TOP && adjType == CornerType.TOP)
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
        }
        else if (side.getAxis() == Direction.Axis.Y && type == SlopeType.HORIZONTAL && ((side == Direction.UP) == (adjType.isTop())))
        {
            if (adjType.isRight())
            {
                return dir == adjDir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (type != SlopeType.HORIZONTAL && adjTop == (type == SlopeType.TOP))
        {
            if (side == dir.rotateY())
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (side == dir.rotateYCCW())
            {
                return adjDir == dir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (type == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y && adjTop == (side == Direction.DOWN))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstInnerThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, SlopeType type, BlockType adjBlock, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); }

        if (type != SlopeType.HORIZONTAL && adjTop == (type == SlopeType.TOP))
        {
            if (side == dir.rotateY())
            {
                return adjDir == dir.rotateY() && SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (side == dir.rotateYCCW())
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (type == SlopeType.HORIZONTAL && side.getAxis() == Direction.Axis.Y && adjTop == (side == Direction.UP))
        {
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }
}