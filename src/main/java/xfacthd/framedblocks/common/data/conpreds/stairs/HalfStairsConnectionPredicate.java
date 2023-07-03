package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class HalfStairsConnectionPredicate implements ConnectionPredicate
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
            return Utils.isY(edge);
        }
        else if (side == facing.getOpposite() || side == dirTwo.getOpposite())
        {
            return edge != dirThree.getOpposite();
        }
        else if (side == dirTwo)
        {
            return edge.getAxis() == facing.getAxis();
        }
        else if (side == dirThree)
        {
            return edge == facing.getOpposite() || edge == dirTwo.getOpposite();
        }
        else if (side == dirThree.getOpposite())
        {
            return true;
        }
        return false;
    }
}
