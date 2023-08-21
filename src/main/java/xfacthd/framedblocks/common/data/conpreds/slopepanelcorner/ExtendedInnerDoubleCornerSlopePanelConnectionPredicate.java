package xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;

public final class ExtendedInnerDoubleCornerSlopePanelConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == dirTwo || side == dir.getOpposite() || side == dir.getClockWise())
        {
            return true;
        }
        else if (side == dirTwo.getOpposite())
        {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        else if (side == dir || side == dir.getCounterClockWise())
        {
            return edge != null && edge != dirTwo.getOpposite();
        }
        return false;
    }
}
