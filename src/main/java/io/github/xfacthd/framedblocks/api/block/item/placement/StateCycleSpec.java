package io.github.xfacthd.framedblocks.api.block.item.placement;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface StateCycleSpec {
    /// Indicates that the block is not aware of manually cycled placement states, this produces startup a warning
    StateCycleSpec NOT_IMPLEMENTED = new NoOpStateCycleSpec.NotImplemented();
    /// Indicates that the block does not support manually cycling through placement states
    StateCycleSpec UNSUPPORTED = new NoOpStateCycleSpec.Unsupported();

    /// Create a spec builder for creating a [StateCycleSpec] for the single provided block.
    static StateCycleSpecBuilder builder(Block block) {
        return new StateCycleSpecBuilder(block);
    }

    /// Create a spec builder for creating a [StateCycleSpec] for multiple blocks (i.e. blocks with separate wall and floor variants).
    static MultiBlockStateCycleSpecBuilder multiBuilder() {
        return new MultiBlockStateCycleSpecBuilder();
    }

    /// Returns whether this [StateCycleSpec] can cycle through states.
    boolean canCycle();

    /// Returns the initial state the provided block can start cycling from.
    /// May only be called if [#canCycle()] returns `true`.
    BlockState getInitialState(@Nullable BlockState placementState);

    /// Cycles the provided state forward or backward depending on the provided direction.
    /// May only be called if [#canCycle()] returns `true`.
    BlockState cycle(BlockState state, boolean forward);

    /// Returns the state to use for placement, configured by prior cycling.
    @Nullable BlockState getPlacementState(BlockPlaceContext context);

    /// Appends the configured placement state's relevant properties to the item tooltip.
    void appendHoverText(Player player, BlockItem item, Consumer<Component> appender);
}
