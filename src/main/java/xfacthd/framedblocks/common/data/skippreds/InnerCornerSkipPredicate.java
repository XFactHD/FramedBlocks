package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class InnerCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }
        if (!(adjState.getBlock() instanceof IFramedBlock)) { return false; }

        BlockType adjBlock = ((IFramedBlock) adjState.getBlock()).getBlockType();
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

        if (adjBlock == BlockType.FRAMED_INNER_CORNER_SLOPE)
        {
            return testAgainstInnerCorner(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_CORNER_SLOPE)
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
        else if (adjBlock == BlockType.FRAMED_PRISM_CORNER || adjBlock == BlockType.FRAMED_THREEWAY_CORNER)
        {
            return testAgainstThreewaySlope(world, pos, dir, type, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_INNER_PRISM_CORNER || adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER)
        {
            return testAgainstInnerThreewaySlope(world, pos, dir, type, adjBlock, adjState, side);
        }
        else if (adjBlock == BlockType.FRAMED_DOUBLE_PRISM_CORNER || adjBlock == BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
        {
            return testAgainstDoubleThreewayCorner(world, pos, dir, type, adjState, side);
        }

        return false;
    }

    private boolean testAgainstInnerCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && adjType == type && ((side == dir.getOpposite() && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && adjDir == dir.rotateYCCW())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal() && type.isHorizontalAdjacentInner(dir, side, adjType) && adjDir == dir)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir.getOpposite() && adjType.isRight() && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && !adjType.isRight() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.rotateY() && adjDir == dir) || (type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateYCCW()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && !adjType.isHorizontal() && adjDir == dir.rotateY() && adjType.isTop() == type.isTop())
        {
            return (side == dir.getOpposite() || side == dir.rotateYCCW()) && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal() && adjType == type && adjDir == dir && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) ||
                (side == dir.rotateY() && !type.isRight()) || (side == dir.rotateYCCW() && type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (!type.isHorizontal() && adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((side == dir.getOpposite() && !adjType.isRight() && adjDir == dir.rotateY()) || (side == dir.rotateYCCW() && adjType.isRight() && adjDir == dir))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal() && !adjType.isHorizontal() && adjType.isTop() == type.isTop() &&
                ((!type.isRight() && side == dir.rotateY() && adjDir == dir) || (type.isRight() && side == dir.rotateYCCW() && adjDir == dir.rotateY()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstDoubleCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        CornerType adjType = adjState.get(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal() && !adjType.isHorizontal())
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getOpposite() && adjDir == dir.getOpposite()) || (side == dir.rotateYCCW() && adjDir == dir)))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (type.isTop() != adjType.isTop() && adjDir == dir.rotateYCCW() && (side == dir.getOpposite() || side == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (type.isHorizontal() && !adjType.isHorizontal())
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.rotateYCCW() && adjDir == dir && type.isRight()) ||
                                                    (side == dir.rotateY() && adjDir == dir.rotateY() && !type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
            else if (type.isTop() != adjType.isTop() && ((side == dir.rotateY() && adjDir == dir.getOpposite() && !type.isRight()) ||
                                                         (side == dir.rotateYCCW() && adjDir == dir.rotateYCCW() && type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else if (!type.isHorizontal()/* && adjType.isHorizontal()*/)
        {
            if (type.isTop() == adjType.isTop() && ((side == dir.getOpposite() && adjDir == dir.rotateY() && adjType.isRight()) ||
                                                    (side == dir.rotateYCCW() && adjDir == dir && !adjType.isRight())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, adjDir);
            }
            else if (type.isTop() != adjType.isTop() && ((side == dir.getOpposite() && adjDir == dir.rotateYCCW() && !adjType.isRight()) ||
                                                         (side == dir.rotateYCCW() && adjDir == dir.getOpposite() && adjType.isRight())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, adjDir.getOpposite());
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
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type.isTop() == adjType.isTop() && ((side == dir.rotateY() && !type.isRight() && adjType.isRight()) ||
                                                             (side == dir.rotateYCCW() && type.isRight() && !adjType.isRight())
                ))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
            else if (adjDir == dir.getOpposite())
            {
                if (type.isRight() == adjType.isRight() && ((side == Direction.UP && !type.isTop() && adjType.isTop()) ||
                                                            (side == Direction.DOWN && type.isTop() && !adjType.isTop())
                ))
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
                else if (type.isRight() == adjType.isRight() && type.isTop() != adjType.isTop() &&
                        ((side == dir.rotateYCCW() && type.isRight()) || (side == dir.rotateY() && !type.isRight()))
                )
                {
                    return SideSkipPredicate.compareState(world, pos, side, dir);
                }
            }
        }
        return false;
    }

    private boolean testAgainstSlope(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        SlopeType adjType = adjState.get(PropertyHolder.SLOPE_TYPE);

        if (!type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.getOpposite() && adjDir == dir.rotateY()) || (side == adjDir.rotateYCCW() && adjDir == dir)))
        {
            return (adjType == SlopeType.TOP) == type.isTop() && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal())
        {
            if (((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())) && adjType == SlopeType.HORIZONTAL)
            {
                return ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.rotateY())) && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (side.getAxis() != Direction.Axis.Y && adjDir == dir && (adjType == SlopeType.TOP) == type.isTop())
            {
                return ((!type.isRight() && side == dir.rotateY()) || (type.isRight() && side == dir.rotateYCCW())) && SideSkipPredicate.compareState(world, pos, side);
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
                (side == dir.getOpposite() && type.isTop() == adjTop && adjDir == dir.rotateY()) ||
                        (side == dir.getOpposite() && type.isTop() != adjTop && adjDir == dir.rotateYCCW()) ||
                        (side == dir.rotateYCCW() && type.isTop() == adjTop && adjDir == dir) ||
                        (side == dir.rotateYCCW() && type.isTop() != adjTop && adjDir == dir.getOpposite())
        ))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return SideSkipPredicate.compareState(world, pos, side, face);
        }
        else if (type.isHorizontal() && adjType == SlopeType.HORIZONTAL && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())))
        {
            if ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW())))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        else if (type.isHorizontal() && adjType != SlopeType.HORIZONTAL && ((side == dir.rotateY() && !type.isRight()) || (side == dir.rotateYCCW() && type.isRight())))
        {
            Direction face = type.isTop() == adjTop ? adjDir : adjDir.getOpposite();
            return ((type.isTop() == adjTop && adjDir == dir) || (type.isTop() != adjTop && adjDir == dir.getOpposite())) && SideSkipPredicate.compareState(world, pos, side, face);
        }
        return false;
    }

    private boolean testAgainstThreewaySlope(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (!type.isHorizontal() && adjDir == dir.rotateY() && adjTop == type.isTop() && (side == dir.getOpposite() || side == dir.rotateYCCW()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal() && ((!type.isRight() && adjDir == dir) || (type.isRight() && adjDir == dir.rotateY())) && adjTop == type.isTop() &&
                ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop()) || (side == dir.rotateY() && !type.isRight()) ||
                        (side == dir.rotateYCCW() && type.isRight()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstInnerThreewaySlope(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockType adjBlock, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (adjBlock == BlockType.FRAMED_INNER_THREEWAY_CORNER) { adjDir = adjDir.rotateY(); } //Correct rotation discrepancy of the threeway corner

        if (!type.isHorizontal() && adjTop == type.isTop() && ((side == dir.getOpposite() && adjDir == dir.getOpposite()) || (side == dir.rotateYCCW() && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (type.isHorizontal())
        {
            if (adjTop != type.isTop() && ((adjDir == dir && !type.isRight()) || (adjDir == dir.rotateY() && type.isRight())) &&
                ((!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (adjTop == type.isTop() && ((!type.isRight() && side == dir.rotateY() && adjDir == dir.rotateY()) ||
                    (type.isRight() && side == dir.rotateYCCW() && adjDir == dir))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }

    private boolean testAgainstDoubleThreewayCorner(IBlockReader world, BlockPos pos, Direction dir, CornerType type, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if (!type.isHorizontal())
        {
            if (adjTop == type.isTop() && ((side == dir.rotateYCCW() && adjDir == dir) || (side == dir.getOpposite() && adjDir == dir.getOpposite())))
            {
                return SideSkipPredicate.compareState(world, pos, side, adjTop ? Direction.UP : Direction.DOWN);
            }
            else if (adjTop != type.isTop() && adjDir == dir.rotateYCCW() && (side == dir.getOpposite() || side == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, side, type.isTop() ? Direction.UP : Direction.DOWN);
            }
        }
        else
        {
            if (adjTop == type.isTop() && ((side == dir.rotateYCCW() && adjDir == dir && type.isRight()) ||
                                           (side == dir.rotateY() && adjDir == dir.rotateY() && !type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (adjTop != type.isTop() && ((side == dir.rotateY() && adjDir == dir.getOpposite() && !type.isRight()) ||
                                                (side == dir.rotateYCCW() && adjDir == dir.rotateYCCW() && type.isRight())
            ))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            else if (adjTop != type.isTop() && ((side == Direction.UP && !type.isTop()) || (side == Direction.DOWN && type.isTop())) &&
                     ((!type.isRight() && (adjDir == dir || adjDir == dir.getOpposite())) || (type.isRight() && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW())))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }
        return false;
    }
}