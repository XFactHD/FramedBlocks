package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class VerticalDoubleHalfSlopeConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dir = top ? Direction.UP : Direction.DOWN;

        if (!DirUtils.isY(side)) {
            return edge == dir;
        }
        if (side == dir) {
            return edge != null;
        }
        return false;
    }
}
