package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deferred holder for [DataComponentType]s.
public final class DeferredDataComponentType<T> extends DeferredHolder<DataComponentType<?>, DataComponentType<T>> {
    private DeferredDataComponentType(ResourceKey<DataComponentType<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the data component type registered under the given name}
    ///
    /// @param name The registry name of the data component type
    public static <T> DeferredDataComponentType<T> createDataComponent(Identifier name) {
        return createDataComponent(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, name));
    }

    /// {@return a deferred holder for the data component type registered under the given key}
    ///
    /// @param key The registry key of the data component type
    public static <T> DeferredDataComponentType<T> createDataComponent(ResourceKey<DataComponentType<?>> key) {
        return new DeferredDataComponentType<>(key);
    }
}
