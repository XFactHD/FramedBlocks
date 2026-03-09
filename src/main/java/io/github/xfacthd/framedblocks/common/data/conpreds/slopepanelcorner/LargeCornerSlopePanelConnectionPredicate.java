package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LargeCornerSlopePanelConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        return side == dirTwo && (edge == dir.getOpposite() || edge == dir.getClockWise());
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dirTwo)
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        else if (side == dir)
        {
            return edge == dir.getCounterClockWise() || DirUtils.isY(edge);
        }
        else if (side == dir.getCounterClockWise())
        {
            return edge == dir || DirUtils.isY(edge);
        }
        else if (side == dirTwo.getOpposite() || side == dir.getOpposite() || side == dir.getClockWise())
        {
            return edge == dir || edge == dir.getCounterClockWise();
        }
        return false;
    }
}
