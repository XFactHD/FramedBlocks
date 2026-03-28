package io.github.xfacthd.framedblocks.common.data.facepreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class InnerCornerFullFacePredicate implements FullFacePredicate {
    public static final InnerCornerFullFacePredicate INSTANCE = new InnerCornerFullFacePredicate();

    private InnerCornerFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if ((type == CornerType.TOP || (type.isHorizontal() && type.isTop())) && side == Direction.UP) {
            return true;
        }
        if ((type == CornerType.BOTTOM || (type.isHorizontal() && !type.isTop())) && side == Direction.DOWN) {
            return true;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (type.isHorizontal()) {
            return facing == side || (type.isRight() && facing.getClockWise() == side) || (!type.isRight() && facing.getCounterClockWise() == side);
        }
        return facing == side || facing.getCounterClockWise() == side;
    }
}
