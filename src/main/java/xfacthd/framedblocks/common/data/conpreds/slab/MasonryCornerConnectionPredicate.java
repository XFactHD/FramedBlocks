package xfacthd.framedblocks.common.data.conpreds.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;

public final class MasonryCornerConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        if (side == bottom)
        {
            return edge == dir || edge == dir.getOpposite();
        }
        else if (side == bottom.getOpposite())
        {
            return edge == dir.getClockWise() || edge == dir.getCounterClockWise();
        }
        else if (side.getAxis() == dir.getAxis())
        {
            return edge == bottom || edge == side.getCounterClockWise();
        }
        else if (side.getAxis() == dir.getClockWise().getAxis())
        {
            return edge == bottom.getOpposite() || edge == side.getClockWise();
        }
        return false;
    }
}
