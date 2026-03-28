package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class PanelConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing) {
            return true;
        }
        return side.getAxis() != facing.getAxis() && edge == facing;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite()) {
            return true;
        }
        if (side.getAxis() != facing.getAxis()) {
            return edge != facing.getOpposite();
        }
        return false;
    }
}
