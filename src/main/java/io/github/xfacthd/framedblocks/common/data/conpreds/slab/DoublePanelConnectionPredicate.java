package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoublePanelConnectionPredicate extends NonDetailedConnectionPredicate {
    public static final DoublePanelConnectionPredicate INSTANCE = new DoublePanelConnectionPredicate();

    private DoublePanelConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side.getAxis() == facing.getAxis()) {
            return true;
        }
        return edge != null && edge.getAxis() == facing.getAxis();
    }
}
