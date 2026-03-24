package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

record IgnoringStateMerger(Set<Property<?>> ignoredProps) implements StateMerger
{
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BlockState apply(BlockState state)
    {
        BlockState defaultState = state.getBlock().defaultBlockState();
        for (Property prop : ignoredProps)
        {
            if (state.hasProperty(prop))
            {
                state = state.setValue(prop, defaultState.getValue(prop));
            }
        }
        return state;
    }

    @Override
    public Set<Property<?>> getHandledProperties(Holder<Block> block)
    {
        return ignoredProps;
    }
}
