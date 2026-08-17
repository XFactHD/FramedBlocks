package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;
import java.util.function.UnaryOperator;

/// "Merges" blockstates to re-use some data for multiple blockstates considered equivalent relative to the data
/// (i.e. re-using models for multiple states which are visually equivalent).
public abstract class StateMerger implements UnaryOperator<BlockState> {
    final Set<Property<?>> handledProperties;

    protected StateMerger(Set<Property<?>> handledProperties) {
        this.handledProperties = handledProperties;
    }

    @Override
    public abstract BlockState apply(BlockState state);

    /// {@return the properties }
    public final Set<Property<?>> getHandledProperties() {
        return handledProperties;
    }
}
