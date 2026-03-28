package io.github.xfacthd.framedblocks.common.data.conpreds.pillar;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public final class PillarSocketConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return side == facing || side.getAxis() != facing.getAxis() && edge == facing;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return side.getAxis() != facing.getAxis() && edge != facing;
    }
}
