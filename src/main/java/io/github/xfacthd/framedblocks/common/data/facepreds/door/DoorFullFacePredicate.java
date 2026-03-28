package io.github.xfacthd.framedblocks.common.data.facepreds.door;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

public final class DoorFullFacePredicate implements FullFacePredicate {
    public static final DoorFullFacePredicate INSTANCE = new DoorFullFacePredicate();

    private DoorFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (state.getValue(BlockStateProperties.OPEN)) {
            if (state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.LEFT) {
                return facing.getCounterClockWise() == side;
            }
            return facing.getClockWise() == side;
        }
        return facing.getOpposite() == side;
    }
}
