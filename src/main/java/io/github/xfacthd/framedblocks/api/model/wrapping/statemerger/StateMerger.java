package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.UnaryOperator;

public interface StateMerger extends UnaryOperator<BlockState>
{
    StateMerger PASSTHROUGH = new PassthroughStateMerger();
    StateMerger IGNORE_ALL = new IgnoreAllStateMerger();

    @Override
    BlockState apply(BlockState state);

    Set<Property<?>> getHandledProperties(Holder<Block> block);

    static StateMerger ignoring(@Nullable Set<Property<?>> ignoredProps)
    {
        if (ignoredProps == null || ignoredProps.isEmpty())
        {
            return PASSTHROUGH;
        }
        return new IgnoringStateMerger(ignoredProps);
    }
}
