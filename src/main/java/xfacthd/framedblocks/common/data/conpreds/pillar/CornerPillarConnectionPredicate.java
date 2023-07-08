package xfacthd.framedblocks.common.data.conpreds.pillar;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

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
        return Utils.isY(side) && edge != dirOne && edge != dirTwo;
    }
}
