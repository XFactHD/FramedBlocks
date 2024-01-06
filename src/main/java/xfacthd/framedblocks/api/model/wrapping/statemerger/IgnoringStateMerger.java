package xfacthd.framedblocks.api.model.wrapping.statemerger;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

import java.util.Set;

record IgnoringStateMerger(Set<Property<?>> ignoredProps) implements StateMerger
{
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BlockState apply(BlockState state)
    {
        BlockState defaultState = state.getBlock().defaultBlockState();
        for (Property prop : ignoredProps)
        {
            if (!state.hasProperty(prop))
            {
                LOGGER.warn("Found invalid ignored property {} for block {}!", prop, state.getBlock());
                continue;
            }
            state = state.setValue(prop, defaultState.getValue(prop));
        }
        return state;
    }

    @Override
    public Set<Property<?>> getHandledProperties(Holder<Block> block)
    {
        return ignoredProps;
    }
}
