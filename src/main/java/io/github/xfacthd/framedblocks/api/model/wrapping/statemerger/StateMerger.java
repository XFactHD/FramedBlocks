package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.UnaryOperator;

/// "Merges" blockstates to re-use some data for multiple blockstates considered equivalent relative to the data
/// (i.e. re-using models for multiple states which are visually equivalent).
public interface StateMerger extends UnaryOperator<BlockState> {
    /// State merger returning every blockstate unmodified.
    StateMerger PASSTHROUGH = new PassthroughStateMerger();
    /// State merger always returning the default state of the block which the querying state is from.
    StateMerger IGNORE_ALL = new IgnoreAllStateMerger();

    @Override
    BlockState apply(BlockState state);

    /// {@return the properties }
    Set<Property<?>> getHandledProperties(Holder<Block> block);

    /// {@return a state merger ignoring (resetting to default value) the given blockstate properties}
    ///
    /// @param ignoredProps The properties to ignore
    static StateMerger ignoring(@Nullable Set<Property<?>> ignoredProps) {
        if (ignoredProps == null || ignoredProps.isEmpty()) {
            return PASSTHROUGH;
        }
        return new IgnoringStateMerger(ignoredProps);
    }
}
