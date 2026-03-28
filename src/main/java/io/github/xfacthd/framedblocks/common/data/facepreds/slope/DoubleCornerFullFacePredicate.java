package io.github.xfacthd.framedblocks.common.data.facepreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DoubleCornerFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal()) {
            if (side == dir || side == dir.getOpposite()) {
                return true;
            }
            if (side == dir.getCounterClockWise()) {
                return !type.isRight();
            }
            if (side == dir.getClockWise()) {
                return type.isRight();
            }
            return (side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop());
        }
        return DirUtils.isY(side) || side == dir || side == dir.getCounterClockWise();
    }
}
