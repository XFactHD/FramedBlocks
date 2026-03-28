package io.github.xfacthd.framedblocks.common.data.facepreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DoubleThreewayCornerFullFacePredicate implements FullFacePredicate {
    public static final DoubleThreewayCornerFullFacePredicate INSTANCE = new DoubleThreewayCornerFullFacePredicate();

    private DoubleThreewayCornerFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if (side == dir || side == dir.getCounterClockWise()) {
            return true;
        }
        return (dir == Direction.DOWN && !top) || (dir == Direction.UP && top);
    }
}
