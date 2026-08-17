package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

final class IgnoringStateMerger extends StateMerger {
    IgnoringStateMerger(Set<Property<?>> ignoredProps) {
        super(ignoredProps);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BlockState apply(BlockState state) {
        BlockState defaultState = state.getBlock().defaultBlockState();
        for (Property prop : handledProperties) {
            if (state.hasProperty(prop)) {
                state = state.setValue(prop, defaultState.getValue(prop));
            }
        }
        return state;
    }
}
