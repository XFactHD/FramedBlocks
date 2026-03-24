package io.github.xfacthd.framedblocks.common.util.registration;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.util.registration.DeferredDataComponentType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public final class DeferredDataComponentTypeRegister extends DeferredRegister.DataComponents
{
    private DeferredDataComponentTypeRegister(String namespace)
    {
        super(Registries.DATA_COMPONENT_TYPE, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends DataComponentType<?>> DeferredHolder<DataComponentType<?>, I> createHolder(
            ResourceKey<? extends Registry<DataComponentType<?>>> registryKey, Identifier key
    )
    {
        return (DeferredHolder<DataComponentType<?>, I>) DeferredDataComponentType.createDataComponent(ResourceKey.create(registryKey, key));
    }

    @Override
    public <D> DeferredDataComponentType<D> registerComponentType(String name, UnaryOperator<DataComponentType.Builder<D>> builder)
    {
        return (DeferredDataComponentType<D>) super.registerComponentType(name, builder);
    }

    public <D> DeferredDataComponentType<D> registerSimpleComponentType(String name, Codec<D> codec, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec)
    {
        return registerComponentType(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec));
    }

    public <D> DeferredDataComponentType<D> registerCachedComponentType(String name, Codec<D> codec, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec)
    {
        return registerComponentType(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec).cacheEncoding());
    }

    public static DeferredDataComponentTypeRegister create(String namespace)
    {
        return new DeferredDataComponentTypeRegister(namespace);
    }
}
