package io.github.xfacthd.framedblocks.common.data.conpreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DividedBoardConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction face = cmpDir.direction();
        Direction dir = cmpDir.orientation();
        if (side == face) {
            return edge == dir || edge == dir.getOpposite();
        }
        if (side == dir || side == dir.getOpposite()) {
            return edge == face;
        }
        return false;
    }
}
