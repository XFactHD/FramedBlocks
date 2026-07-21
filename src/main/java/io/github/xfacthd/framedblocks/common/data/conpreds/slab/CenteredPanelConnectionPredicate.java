package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public final class CenteredPanelConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        return side.getAxis() == axis || edge.getAxis() != axis;
    }
}
