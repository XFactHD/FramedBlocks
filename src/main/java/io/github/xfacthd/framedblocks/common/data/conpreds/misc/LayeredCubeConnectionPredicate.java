package io.github.xfacthd.framedblocks.common.data.conpreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public final class LayeredCubeConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        int layers = state.getValue(BlockStateProperties.LAYERS);
        if (layers == 8) {
            return true;
        }

        Direction facing = state.getValue(BlockStateProperties.FACING);
        return side == facing.getOpposite() || (side != facing && edge == facing.getOpposite());
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        int layers = state.getValue(BlockStateProperties.LAYERS);
        if (layers == 8) {
            return false;
        }

        Direction facing = state.getValue(BlockStateProperties.FACING);
        return side == facing || (side != facing.getOpposite() && edge.getAxis() != facing.getAxis());
    }
}
