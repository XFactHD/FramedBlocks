package io.github.xfacthd.framedblocks.common.data.facepreds.door;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

public final class TrapdoorFullFacePredicate implements FullFacePredicate {
    public static final TrapdoorFullFacePredicate INSTANCE = new TrapdoorFullFacePredicate();

    private TrapdoorFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        if (state.getValue(BlockStateProperties.OPEN)) {
            if (state.getValue(PropertyHolder.ROTATE_TEXTURE)) {
                return false;
            }
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite() == side;
        }
        if (state.getValue(BlockStateProperties.HALF) == Half.TOP) {
            return side == Direction.UP;
        }
        return side == Direction.DOWN;
    }
}
