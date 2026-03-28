package io.github.xfacthd.framedblocks.api.util.registration;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredMapCodec<T> extends DeferredHolder<MapCodec<?>, MapCodec<T>> {
    private DeferredMapCodec(ResourceKey<MapCodec<?>> key) {
        super(key);
    }

    public static <T> DeferredMapCodec<T> createCodec(ResourceKey<MapCodec<?>> key) {
        return new DeferredMapCodec<>(key);
    }
}
