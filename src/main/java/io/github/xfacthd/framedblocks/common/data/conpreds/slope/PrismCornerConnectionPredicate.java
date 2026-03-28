package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class PrismCornerConnectionPredicate extends ThreewayCornerConnectionPredicate {
    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.DOWN : Direction.UP;

        if (side == facing.getOpposite() || side == facing.getClockWise() || side == dirTwo) {
            return edge == dirTwo.getOpposite();
        }
        return false;
    }
}
