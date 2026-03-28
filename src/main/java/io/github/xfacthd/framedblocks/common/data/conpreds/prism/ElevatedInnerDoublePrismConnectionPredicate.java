package io.github.xfacthd.framedblocks.common.data.conpreds.prism;

import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class ElevatedInnerDoublePrismConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return side.getAxis() != dirAxis.axis() || edge != null;
    }
}
