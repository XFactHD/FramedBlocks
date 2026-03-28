package io.github.xfacthd.framedblocks.common.data.conpreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class CollapsibleBlockConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction face = state.getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection();
        if (face == null || side == face.getOpposite()) {
            return true;
        } else if (side.getAxis() != face.getAxis()) {
            return edge == face.getOpposite();
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction face = state.getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection();
        if (face != null && (side == face || side.getAxis() != face.getAxis())) {
            return edge.getAxis() != face.getAxis();
        }
        return false;
    }
}
