package io.github.xfacthd.framedblocks.api.block.item.placement;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

abstract sealed class NoOpStateCycleSpec implements StateCycleSpec {
    @Override
    public final boolean canCycle() {
        return false;
    }

    @Override
    public final BlockState cycle(BlockState state, boolean forward) {
        throw new UnsupportedOperationException("Cannot cycle state with NoOpStateCycleSpec");
    }

    @Override
    public final BlockState getInitialState(@Nullable BlockState placementState) {
        throw new UnsupportedOperationException("Cannot query initial state of NoOpStateCycleSpec");
    }

    @Override
    public final @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        return null;
    }

    @Override
    public final void appendHoverText(Player player, BlockItem item, Consumer<Component> appender) { }

    static final class NotImplemented extends NoOpStateCycleSpec {
        NotImplemented() { }
    }

    static final class Unsupported extends NoOpStateCycleSpec {
        Unsupported() { }
    }
}
