package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleSlopeConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL) {
            return !DirUtils.isY(side) || edge != null;
        }

        Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
        if (side.getAxis() == dirOne.getAxis() || side.getAxis() == dirTwo.getAxis()) {
            return true;
        }
        return edge != null;
    }
}
