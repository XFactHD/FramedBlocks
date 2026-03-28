package io.github.xfacthd.framedblocks.api.internal.duck;

import net.minecraft.world.level.block.state.properties.Property;

public interface StateDefinitionBuilderAccessor {
    default boolean framedblocks$hasProperty(Property<?> property) {
        throw new AssertionError();
    }

    default void framedblocks$removeProperty(Property<?> property) {
        throw new AssertionError();
    }
}
