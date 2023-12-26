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
        if (side == Direction.DOWN)
        {
            return edge == dir || edge == dir.getOpposite();
        }
        else if (side == Direction.UP)
        {
            return edge == dir.getClockWise() || edge == dir.getCounterClockWise();
        }
        else if (side.getAxis() == dir.getAxis())
        {
            return edge == Direction.DOWN || edge == side.getCounterClockWise();
        }
        else if (side.getAxis() == dir.getClockWise().getAxis())
        {
            return edge == Direction.UP || edge == side.getClockWise();
        }
        return false;
    }
}
