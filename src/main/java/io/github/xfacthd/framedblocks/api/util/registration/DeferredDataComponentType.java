package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredDataComponentType<T> extends DeferredHolder<DataComponentType<?>, DataComponentType<T>>
{
    private DeferredDataComponentType(ResourceKey<DataComponentType<?>> key)
    {
        super(key);
    }

    public static <T> DeferredDataComponentType<T> createDataComponent(Identifier name)
    {
        return createDataComponent(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, name));
    }

    public static <T> DeferredDataComponentType<T> createDataComponent(ResourceKey<DataComponentType<?>> key)
    {
        return new DeferredDataComponentType<>(key);
    }
}
