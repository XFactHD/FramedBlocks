package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public final class ElevatedPyramidSlabConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (side == facing.getOpposite()) {
            return true;
        }
        return side.getAxis() != facing.getAxis() && edge == facing.getOpposite();
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (edge == facing) {
            return state.getValue(PropertyHolder.PILLAR_CONNECTION) != PillarConnection.NONE;
        }
        if (side.getAxis() != facing.getAxis()) {
            return edge.getAxis() != facing.getAxis();
        }
        return false;
    }
}
