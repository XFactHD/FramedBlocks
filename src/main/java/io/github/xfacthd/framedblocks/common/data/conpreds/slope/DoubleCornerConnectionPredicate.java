package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleCornerConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal()) {
            Direction dirTwo = type.isTop() ? Direction.UP : Direction.DOWN;
            Direction dirThree = type.isRight() ? facing.getClockWise() : facing.getCounterClockWise();
            if (side.getAxis() == facing.getAxis() || side == dirTwo || side == dirThree) {
                return true;
            }
        } else if (side == facing || side == facing.getCounterClockWise() || DirUtils.isY(side)) {
            return true;
        }
        return edge != null;
    }
}
