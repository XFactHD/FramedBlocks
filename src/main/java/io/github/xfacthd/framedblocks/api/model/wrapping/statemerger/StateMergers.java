package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/// Holds singleton instances and factory methods for standard state mergers.
public final class StateMergers {
    /// Set of properties that all blocks should ignore when wrapping models.
    public static final Set<Property<?>> IGNORED_PROPS = Utils.concat(BlockUtils.REQUIRED_STATE_PROPERTIES, Set.of(
            FramedProperties.SOLID,
            FramedProperties.PROPAGATES_SKYLIGHT,
            FramedProperties.GLOWING,
            BlockStateProperties.WATERLOGGED,
            FramedProperties.STATE_LOCKED
    ));
    /// State merger returning every blockstate unmodified.
    public static final StateMerger PASSTHROUGH = new PassthroughStateMerger();
    /// State merger for blocks which need to ignore the default properties and [BlockStateProperties#POWERED].
    public static final StateMerger POWERED = ignoring(Utils.concat(IGNORED_PROPS, Set.of(BlockStateProperties.POWERED)));
    /// Default state merger to use for all blocks which only need to ignore the above properties.
    public static final StateMerger DEFAULT = ignoring(IGNORED_PROPS);

    /// {@return a state merger ignoring (resetting to default value) the given blockstate properties}
    ///
    /// @param ignoredProps The properties to ignore
    public static StateMerger ignoring(@Nullable Set<Property<?>> ignoredProps) {
        if (ignoredProps == null || ignoredProps.isEmpty()) {
            return PASSTHROUGH;
        }
        return new IgnoringStateMerger(ignoredProps);
    }

    /// {@return a state merger that always returns the default state of the given block}
    ///
    /// @param block The block to use this merger for
    public static StateMerger ignoreAll(Holder<Block> block) {
        return new IgnoreAllStateMerger(block);
    }

    /// {@return a state merger applying both given mergers}
    ///
    /// @param mergerOne The first merger to apply
    /// @param mergerTwo The second merger to apply
    public static StateMerger compound(StateMerger mergerOne, StateMerger mergerTwo) {
        return CompoundStateMerger.of(mergerOne, mergerTwo);
    }

    private StateMergers() { }
}
