package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredRecipeSerializer<T extends Recipe<?>> extends DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>>
{
    private DeferredRecipeSerializer(ResourceKey<RecipeSerializer<?>> key)
    {
        super(key);
    }

    public static <T extends Recipe<?>> DeferredRecipeSerializer<T> createRecipeSerializer(Identifier name)
    {
        return createRecipeSerializer(ResourceKey.create(Registries.RECIPE_SERIALIZER, name));
    }

    public static <T extends Recipe<?>> DeferredRecipeSerializer<T> createRecipeSerializer(ResourceKey<RecipeSerializer<?>> key)
    {
        return new DeferredRecipeSerializer<>(key);
    }
}
