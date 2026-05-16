package io.github.xfacthd.framedblocks.api.block.item.placement;

import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.world.level.block.Block;

import java.util.SequencedMap;
import java.util.function.UnaryOperator;

public final class MultiBlockStateCycleSpecBuilder {
    private final SequencedMap<Block, StateCycleSpecBuilder> entries = new Reference2ObjectLinkedOpenHashMap<>();

    MultiBlockStateCycleSpecBuilder() {}

    /// Add a spec builder for the provided block
    ///
    /// @param block   The block to create a spec builder for
    /// @param builder A lambda configuring the created spec builder
    public MultiBlockStateCycleSpecBuilder add(Block block, UnaryOperator<StateCycleSpecBuilder> builder) {
        if (entries.containsKey(block)) {
            throw new IllegalStateException("Duplicate entry for " + block);
        }
        entries.put(block, builder.apply(new StateCycleSpecBuilder(block)));
        return this;
    }

    public StateCycleSpec build() {
        return InternalAPI.INSTANCE.buildMultiBlockStateCycleSpec(entries, StateCycleSpecBuilder::assemble);
    }
}
