package io.github.xfacthd.framedblocks.common.util.registration;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.util.registration.DeferredRecipeSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DeferredRecipeSerializerRegister extends DeferredRegister<RecipeSerializer<?>>
{
    private DeferredRecipeSerializerRegister(String namespace)
    {
        super(Registries.RECIPE_SERIALIZER, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends RecipeSerializer<?>> DeferredHolder<RecipeSerializer<?>, I> createHolder(
            ResourceKey<? extends Registry<RecipeSerializer<?>>> registryKey, Identifier key
    )
    {
        return (DeferredHolder<RecipeSerializer<?>, I>) DeferredRecipeSerializer.createRecipeSerializer(ResourceKey.create(registryKey, key));
    }

    public <R extends Recipe<?>> DeferredRecipeSerializer<R> registerRecipeSerializer(
            String name, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec
    )
    {
        Holder<RecipeSerializer<?>> holder = register(name, () -> new RecipeSerializer<>(codec, streamCodec));
        return (DeferredRecipeSerializer<R>) holder;
    }

    public static DeferredRecipeSerializerRegister create(String namespace)
    {
        return new DeferredRecipeSerializerRegister(namespace);
    }
}
