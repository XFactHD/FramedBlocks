package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;

public final class DividedStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing)
        {
            if (edge == facing.getCounterClockWise() && shape != StairsShape.OUTER_RIGHT)
            {
                return true;
            }
            if (edge == facing.getClockWise() && shape != StairsShape.OUTER_LEFT)
            {
                return true;
            }
        }
        else if (side == dirTwo)
        {
            return edge != null && edge.getAxis() == facing.getClockWise().getAxis();
        }
        else if (side == dirTwo.getOpposite() || side == facing.getOpposite())
        {
            if (shape == StairsShape.INNER_LEFT && edge == facing.getCounterClockWise())
            {
                return true;
            }
            if (shape == StairsShape.INNER_RIGHT && edge == facing.getClockWise())
            {
                return true;
            }
        }
        else if (side == facing.getCounterClockWise())
        {
            if (shape == StairsShape.INNER_LEFT)
            {
                return true;
            }
            if (shape != StairsShape.OUTER_RIGHT)
            {
                return edge == facing || edge == dirTwo;
            }
        }
        else if (side == facing.getClockWise())
        {
            if (shape == StairsShape.INNER_RIGHT)
            {
                return true;
            }
            if (shape != StairsShape.OUTER_LEFT)
            {
                return edge == facing || edge == dirTwo;
            }
        }
        return false;
    }
}
