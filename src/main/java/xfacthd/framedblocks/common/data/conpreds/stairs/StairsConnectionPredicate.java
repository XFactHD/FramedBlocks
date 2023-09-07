package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class StairsConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
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
                    yield edge == facing;
                }
                else if (side == facing.getOpposite())
                {
                    yield edge == dirTwo;
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
                    yield edge == dirTwo;
                }
                yield false;
            }
            case OUTER_RIGHT ->
            {
                if (side == dirTwo)
                {
                    yield true;
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
                    yield edge == dirTwo;
                }
                yield false;
            }
        };
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        return switch (shape)
        {
            case STRAIGHT ->
            {
                if (side == dirTwo.getOpposite())
                {
                    yield edge != facing;
                }
                else if (side == facing.getOpposite())
                {
                    yield edge != dirTwo;
                }
                else if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    yield edge == dirTwo.getOpposite();
                }
                yield false;
            }
            case INNER_LEFT ->
            {
                if (side == dirTwo.getOpposite())
                {
                    yield edge == facing.getOpposite() || edge == facing.getClockWise();
                }
                else if (side == facing.getOpposite())
                {
                    yield edge == facing.getClockWise() || edge == dirTwo.getOpposite();
                }
                else if (side == facing.getClockWise())
                {
                    yield edge == facing.getOpposite() || edge == dirTwo.getOpposite();
                }
                yield false;
            }
            case INNER_RIGHT ->
            {
                if (side == dirTwo.getOpposite())
                {
                    yield edge == facing.getOpposite() || edge == facing.getCounterClockWise();
                }
                else if (side == facing.getOpposite())
                {
                    yield edge == facing.getCounterClockWise() || edge == dirTwo.getOpposite();
                }
                else if (side == facing.getCounterClockWise())
                {
                    yield edge == facing.getOpposite() || edge == dirTwo.getOpposite();
                }
                yield false;
            }
            case OUTER_LEFT ->
            {
                if (side == dirTwo.getOpposite())
                {
                    yield true;
                }
                else if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield edge != dirTwo;
                }
                else if (side == facing)
                {
                    yield edge == dirTwo.getOpposite() || edge == facing.getClockWise();
                }
                else if (side == facing.getCounterClockWise())
                {
                    yield edge == dirTwo.getOpposite() || edge == facing.getOpposite();
                }
                yield false;
            }
            case OUTER_RIGHT ->
            {
                if (side == dirTwo.getOpposite())
                {
                    yield true;
                }
                else if (side == facing.getOpposite() || side == facing.getCounterClockWise())
                {
                    yield edge != dirTwo;
                }
                else if (side == facing)
                {
                    yield edge == dirTwo.getOpposite() || edge == facing.getCounterClockWise();
                }
                else if (side == facing.getClockWise())
                {
                    yield edge == dirTwo.getOpposite() || edge == facing.getOpposite();
                }
                yield false;
            }
        };
    }
}
