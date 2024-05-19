package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class SlicedStairsSlabConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo)
        {
            return true;
        }
        else if (side == dirTwo.getOpposite())
        {
            if (edge == dir)
            {
                return shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT;
            }
            if (shape == StairsShape.INNER_LEFT && edge == dir.getCounterClockWise())
            {
                return true;
            }
            if (shape == StairsShape.INNER_RIGHT && edge == dir.getClockWise())
            {
                return true;
            }
        }
        else if (!Utils.isY(side) && edge == dirTwo)
        {
            return true;
        }
        else if (side == dir && edge == dirTwo.getOpposite())
        {
            return shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT;
        }
        else if (side == dir.getCounterClockWise() && edge == dirTwo.getOpposite())
        {
            return shape == StairsShape.INNER_LEFT;
        }
        else if (side == dir.getClockWise() && edge == dirTwo.getOpposite())
        {
            return shape == StairsShape.INNER_RIGHT;
        }
        return false;
    }
}
