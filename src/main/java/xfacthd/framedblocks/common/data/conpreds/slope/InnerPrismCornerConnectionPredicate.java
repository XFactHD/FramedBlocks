package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;

public final class InnerPrismCornerConnectionPredicate extends InnerThreewayCornerConnectionPredicate
{
    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing.getOpposite() || side == facing.getClockWise() || side == dirTwo)
        {
            return edge == dirTwo;
        }
        return false;
    }
}
