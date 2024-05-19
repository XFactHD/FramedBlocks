package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class DoubleStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        return switch (shape)
        {
            case STRAIGHT ->
            {
                if (side == facing || side == dirTwo)
                {
                    yield true;
                }
                else if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    yield edge == facing || edge == dirTwo;
                }
                else if (side == dirTwo.getOpposite())
                {
                    yield edge != null && edge.getAxis() == facing.getAxis();
                }
                else if (side == facing.getOpposite())
                {
                    yield edge != null && Utils.isY(edge);
                }
                yield false;
            }
            case INNER_LEFT ->
            {
                if (side == facing || side == facing.getCounterClockWise() || side == dirTwo)
                {
                    yield true;
                }
                else if (side == facing.getClockWise())
                {
                    yield edge == facing || edge == dirTwo;
                }
                else if (side == facing.getOpposite())
                {
                    yield edge == facing.getCounterClockWise() || edge == dirTwo;
                }
                else if (side == dirTwo.getOpposite())
                {
                    yield edge == facing || edge == facing.getCounterClockWise();
                }
                yield false;
            }
            case INNER_RIGHT ->
            {
                if (side == facing || side == facing.getClockWise() || side == dirTwo)
                {
                    yield true;
                }
                else if (side == facing.getCounterClockWise())
                {
                    yield edge == facing || edge == dirTwo;
                }
                else if (side == facing.getOpposite())
                {
                    yield edge == facing.getClockWise() || edge == dirTwo;
                }
                else if (side == dirTwo.getOpposite())
                {
                    yield edge == facing || edge == facing.getClockWise();
                }
                yield false;
            }
            case OUTER_LEFT ->
            {
                if (side == dirTwo)
                {
                    yield true;
                }
                else if (side == dirTwo.getOpposite())
                {
                    yield edge == facing.getClockWise() || edge == facing.getOpposite();
                }
                else if (side == facing)
                {
                    yield edge == facing.getCounterClockWise() || edge == dirTwo;
                }
                else if (side == facing.getCounterClockWise())
                {
                    yield edge == facing || edge == dirTwo;
                }
                else if (side == facing.getClockWise() || side == facing.getOpposite())
                {
                    yield edge != null && Utils.isY(edge);
                }
                yield false;
            }
            case OUTER_RIGHT ->
            {
                if (side == dirTwo)
                {
                    yield true;
                }
                else if (side == dirTwo.getOpposite())
                {
                    yield edge == facing.getCounterClockWise() || edge == facing.getOpposite();
                }
                else if (side == facing)
                {
                    yield edge == facing.getClockWise() || edge == dirTwo;
                }
                else if (side == facing.getClockWise())
                {
                    yield edge == facing || edge == dirTwo;
                }
                else if (side == facing.getCounterClockWise() || side == facing.getOpposite())
                {
                    yield edge != null && Utils.isY(edge);
                }
                yield false;
            }
        };
    }
}
