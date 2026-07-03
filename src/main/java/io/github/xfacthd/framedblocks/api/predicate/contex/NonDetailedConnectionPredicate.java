package io.github.xfacthd.framedblocks.api.predicate.contex;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/// Base connection predicate implementation for blocks which only support full-edge connections.
public abstract class NonDetailedConnectionPredicate implements ConnectionPredicate {
    @Override
    public final boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        return false;
    }
}
