package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;

public final class DoubleThreewayCornerConnectionPredicate extends NonDetailedConnectionPredicate
{
    public static final DoubleThreewayCornerConnectionPredicate INSTANCE = new DoubleThreewayCornerConnectionPredicate();

    private DoubleThreewayCornerConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise() || side == dirTwo)
        {
            return true;
        }
        return edge != null;
    }
}
