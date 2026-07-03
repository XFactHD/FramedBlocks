package io.github.xfacthd.framedblocks.api.util.registration;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deferred holder for [MapCodec]s.
public final class DeferredMapCodec<T> extends DeferredHolder<MapCodec<?>, MapCodec<T>> {
    private DeferredMapCodec(ResourceKey<MapCodec<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the map registered under the given key}
    ///
    /// @param key The registry key of the map codec
    public static <T> DeferredMapCodec<T> createCodec(ResourceKey<MapCodec<?>> key) {
        return new DeferredMapCodec<>(key);
    }
}
