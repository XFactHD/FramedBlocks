package io.github.xfacthd.framedblocks.common.data.facepreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.block.SlopeBlock;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class SlopeFullFacePredicate implements FullFacePredicate {
    public static final SlopeFullFacePredicate INSTANCE = new SlopeFullFacePredicate();

    private SlopeFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        SlopeBlock block = (SlopeBlock) state.getBlock();
        SlopeType type = block.getSlopeType(state);
        if (side == Direction.UP && type == SlopeType.TOP) {
            return true;
        }
        if (side == Direction.DOWN && type == SlopeType.BOTTOM) {
            return true;
        }
        if (type == SlopeType.HORIZONTAL) {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return side == facing || side == facing.getCounterClockWise();
        }
        return block.getFacing(state) == side;
    }
}
