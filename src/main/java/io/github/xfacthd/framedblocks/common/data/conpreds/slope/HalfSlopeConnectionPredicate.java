package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class HalfSlopeConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        Direction dirThree = right ? facing.getClockWise() : facing.getCounterClockWise();

        if (side == facing || side == dirTwo)
        {
            return edge == dirThree;
        }
        else if (side == dirThree)
        {
            return edge == facing || edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        Direction dirThree = right ? facing.getClockWise() : facing.getCounterClockWise();

        if (side == facing)
        {
            return DirUtils.isY(edge);
        }
        else if (side == facing.getOpposite() || side == dirTwo.getOpposite())
        {
            return edge == dirThree;
        }
        else if (side == dirTwo)
        {
            return edge.getAxis() == facing.getAxis();
        }
        else if (side == dirThree.getOpposite())
        {
            return edge == facing || edge == dirTwo;
        }
        return false;
    }
}
