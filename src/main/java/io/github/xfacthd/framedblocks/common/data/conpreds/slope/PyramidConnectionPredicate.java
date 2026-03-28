package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public final class PyramidConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        return side == state.getValue(BlockStateProperties.FACING).getOpposite();
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        if (state.getValue(PropertyHolder.PILLAR_CONNECTION) != PillarConnection.NONE) {
            return edge == state.getValue(BlockStateProperties.FACING);
        }
        return false;
    }
}
