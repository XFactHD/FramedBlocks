package xfacthd.framedblocks.common.data.conpreds.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class MasonryCornerSegmentConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == Direction.DOWN)
        {
            return edge == dir.getOpposite();
        }
        else if (side == Direction.UP)
        {
            return edge == dir.getClockWise();
        }
        else if (side == dir.getOpposite())
        {
            return edge == Direction.DOWN || edge == side.getCounterClockWise();
        }
        else if (side == dir.getClockWise())
        {
            return edge == Direction.UP || edge == side.getClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == Direction.DOWN)
        {
            return edge != dir.getOpposite();
        }
        else if (side == Direction.UP)
        {
            return edge != dir.getClockWise();
        }
        else if (side == dir || side == dir.getCounterClockWise())
        {
            return true;
        }
        else if (side == dir.getOpposite())
        {
            return edge == Direction.UP || edge == side.getClockWise();
        }
        else if (side == dir.getClockWise())
        {
            return edge == Direction.DOWN || edge == side.getCounterClockWise();
        }
        return false;
    }
}
