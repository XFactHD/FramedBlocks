package io.github.xfacthd.framedblocks.common.util.registration;

import io.github.xfacthd.framedblocks.api.util.registration.DeferredRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DeferredRecipeTypeRegister extends DeferredRegister<RecipeType<?>>
{
    private DeferredRecipeTypeRegister(String namespace)
    {
        super(Registries.RECIPE_TYPE, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends RecipeType<?>> DeferredHolder<RecipeType<?>, I> createHolder(
            ResourceKey<? extends Registry<RecipeType<?>>> registryKey, Identifier key
    )
    {
        return (DeferredHolder<RecipeType<?>, I>) DeferredRecipeType.createRecipeType(ResourceKey.create(registryKey, key));
    }

    public <R extends Recipe<?>> DeferredRecipeType<R> registerRecipeType(String name)
    {
        Identifier location = Identifier.fromNamespaceAndPath(getNamespace(), name);
        Holder<RecipeType<?>> holder = register(name, () -> RecipeType.simple(location));
        return (DeferredRecipeType<R>) holder;
    }

    public static DeferredRecipeTypeRegister create(String namespace)
    {
        return new DeferredRecipeTypeRegister(namespace);
    }
}
