package xfacthd.framedblocks.common.data.conpreds.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public final class SlabCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dirOne = state.getValue(FramedProperties.FACING_HOR).getOpposite();
        Direction dirTwo = dirOne.getCounterClockWise();
        Direction dirThree = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        if (side == dirOne || side == dirOne.getOpposite())
        {
            return edge != dirTwo && edge != dirThree;
        }
        if (side == dirTwo || side == dirTwo.getOpposite())
        {
            return edge != dirOne && edge != dirThree;
        }
        if (side == dirThree || side == dirThree.getOpposite())
        {
            return edge != dirOne && edge != dirTwo;
        }
        return false;
    }
}
