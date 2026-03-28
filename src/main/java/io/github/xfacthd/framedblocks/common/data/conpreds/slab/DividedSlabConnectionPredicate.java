package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DividedSlabConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
            return edge == facing || edge == facing.getOpposite();
        }
        if ((!top && edge == Direction.DOWN) || (top && edge == Direction.UP)) {
            return side == facing || side == facing.getOpposite();
        }
        return false;
    }
}
