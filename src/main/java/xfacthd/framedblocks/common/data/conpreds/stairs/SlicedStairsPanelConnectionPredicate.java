package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class SlicedStairsPanelConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dir)
        {
            if (shape == StairsShape.STRAIGHT || shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT)
            {
                return true;
            }
            if (edge == dir.getCounterClockWise() && shape != StairsShape.OUTER_RIGHT)
            {
                return true;
            }
            if (edge == dir.getClockWise() && shape != StairsShape.OUTER_LEFT)
            {
                return true;
            }
            if (edge != null && Utils.isY(edge))
            {
                return shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT;
            }
            return false;
        }
        if (side == dir.getOpposite())
        {
            if (edge == dir.getCounterClockWise())
            {
                return shape == StairsShape.INNER_LEFT;
            }
            if (edge == dir.getClockWise())
            {
                return shape == StairsShape.INNER_RIGHT;
            }
            if (edge == dirTwo)
            {
                return shape != StairsShape.INNER_LEFT && shape != StairsShape.INNER_RIGHT;
            }
            return false;
        }
        if (side == dir.getCounterClockWise())
        {
            if (shape == StairsShape.INNER_LEFT)
            {
                return true;
            }
            return (edge == dir && shape != StairsShape.OUTER_RIGHT) || (edge == dirTwo && shape == StairsShape.OUTER_RIGHT);
        }
        if (side == dir.getClockWise())
        {
            if (shape == StairsShape.INNER_RIGHT)
            {
                return true;
            }
            return (edge == dir && shape != StairsShape.OUTER_LEFT) || (edge == dirTwo && shape == StairsShape.OUTER_LEFT);
        }
        if (side == dirTwo)
        {
            if (edge == dir)
            {
                return shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT;
            }
            if (edge == dir.getOpposite())
            {
                return shape != StairsShape.INNER_LEFT && shape != StairsShape.INNER_RIGHT;
            }
            if (edge == dir.getCounterClockWise())
            {
                return shape == StairsShape.OUTER_RIGHT || shape == StairsShape.INNER_LEFT;
            }
            if (edge == dir.getClockWise())
            {
                return shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_RIGHT;
            }
            return false;
        }
        if (side == dirTwo.getOpposite())
        {
            if (edge == dir)
            {
                return shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT;
            }
            if (edge == dir.getCounterClockWise())
            {
                return shape == StairsShape.INNER_LEFT;
            }
            if (edge == dir.getClockWise())
            {
                return shape == StairsShape.INNER_RIGHT;
            }
        }
        return false;
    }
}
