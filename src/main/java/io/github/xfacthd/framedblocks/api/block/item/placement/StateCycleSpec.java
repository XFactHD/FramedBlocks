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

/// Specification of how to manually cycle through a block's possible placement states.
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

    /// Returns the initial state the provided block can start cycling from, optionally based on the
    /// provided state origination from a [BlockItem#getPlacementState(BlockPlaceContext)] call
    /// without state cycling enabled.
    /// May only be called if [#canCycle()] returns `true`.
    ///
    /// @param placementState The automatic placement state, if available
    BlockState getInitialState(@Nullable BlockState placementState);

    /// Cycles the provided state forward or backward depending on the provided direction.
    /// May only be called if [#canCycle()] returns `true`.
    /// The given state must be one provided by [#getInitialState(BlockState)] or a prior call to [#cycle(BlockState, boolean)].
    ///
    /// @param state   The placement state from which to cycle
    /// @param forward Whether to cycle forward or backward
    BlockState cycle(BlockState state, boolean forward);

    /// Returns the state to use for placement, configured by prior cycling.
    ///
    /// @param context The context used for placing the block
    @Nullable BlockState getPlacementState(BlockPlaceContext context);

    /// Appends the configured placement state's relevant properties to the item tooltip.
    ///
    /// @param player   The player to whom the tooltip is displayed
    /// @param item     The item on which the tooltip is displayed
    /// @param appender The appender to pass the tooltip lines to
    void appendHoverText(Player player, BlockItem item, Consumer<Component> appender);
}
