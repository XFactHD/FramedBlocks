package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LargePrismSlopePanelCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction botDir = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (side == dir)
        {
            return edge == dir.getCounterClockWise() || edge == botDir;
        }
        if (side == dir.getCounterClockWise())
        {
            return edge == dir || edge == botDir;
        }
        if (side == botDir)
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction topDir = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        if (side == topDir)
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        if (side == dir || side == dir.getCounterClockWise())
        {
            return edge == topDir;
        }
        if (side == dir.getOpposite() || side == dir.getClockWise())
        {
            return Utils.isY(edge);
        }
        return false;
    }
}
