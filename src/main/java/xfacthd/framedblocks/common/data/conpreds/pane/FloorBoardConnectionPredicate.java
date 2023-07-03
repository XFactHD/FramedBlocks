package xfacthd.framedblocks.common.data.conpreds.pane;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class FloorBoardConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (side == dir)
        {
            return true;
        }
        return !Utils.isY(side) && edge == dir;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        if (side == dir)
        {
            return true;
        }
        return !Utils.isY(side) && edge.getAxis() != dir.getAxis();
    }
}
