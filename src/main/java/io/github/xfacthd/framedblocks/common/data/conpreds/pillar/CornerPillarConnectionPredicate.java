package io.github.xfacthd.framedblocks.common.data.conpreds.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class CornerPillarConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = dirOne.getCounterClockWise();
        return (side == dirOne && edge == dirTwo) || (side == dirTwo && edge == dirOne);
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dirOne = state.getValue(FramedProperties.FACING_HOR).getOpposite();
        Direction dirTwo = dirOne.getCounterClockWise();
        if (side == dirOne || side == dirOne.getOpposite())
        {
            return edge != dirTwo;
        }
        if (side == dirTwo || side == dirTwo.getOpposite())
        {
            return edge != dirOne;
        }
        return DirUtils.isY(side) && edge != dirOne && edge != dirTwo;
    }
}
