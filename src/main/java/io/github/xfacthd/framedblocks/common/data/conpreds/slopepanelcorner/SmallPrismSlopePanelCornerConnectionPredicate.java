package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SmallPrismSlopePanelCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir)
        {
            return edge == dir.getCounterClockWise();
        }
        if (side == dir.getCounterClockWise())
        {
            return edge == dir;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction botDir = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (side == botDir)
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        if (!DirUtils.isY(side))
        {
            return edge == botDir;
        }
        return false;
    }
}
