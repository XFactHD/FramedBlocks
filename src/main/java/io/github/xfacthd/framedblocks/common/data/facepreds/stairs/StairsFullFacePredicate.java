package io.github.xfacthd.framedblocks.common.data.facepreds.stairs;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

public final class StairsFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        if (side == Direction.UP) {
            return state.getValue(BlockStateProperties.HALF) == Half.TOP;
        }
        if (side == Direction.DOWN) {
            return state.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
        }

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
        if (shape == StairsShape.STRAIGHT) {
            return facing == side;
        }
        if (shape == StairsShape.INNER_LEFT) {
            return facing == side || facing.getCounterClockWise() == side;
        }
        if (shape == StairsShape.INNER_RIGHT) {
            return facing == side || facing.getClockWise() == side;
        }
        return false;
    }
}
