package io.github.xfacthd.framedblocks.common.data.facepreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class CornerFullFacePredicate implements FullFacePredicate {
    public static final CornerFullFacePredicate INSTANCE = new CornerFullFacePredicate();

    private CornerFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.TOP) {
            return side == Direction.UP;
        }
        if (type == CornerType.BOTTOM) {
            return side == Direction.DOWN;
        }
        return state.getValue(FramedProperties.FACING_HOR) == side;
    }
}
