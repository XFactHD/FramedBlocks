package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deferred holder for [RecipeSerializer]s.
public final class DeferredRecipeSerializer<T extends Recipe<?>> extends DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> {
    private DeferredRecipeSerializer(ResourceKey<RecipeSerializer<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the recipe serializer registered under the given name}
    ///
    /// @param name The registry name of the recipe serializer
    public static <T extends Recipe<?>> DeferredRecipeSerializer<T> createRecipeSerializer(Identifier name) {
        return createRecipeSerializer(ResourceKey.create(Registries.RECIPE_SERIALIZER, name));
    }

    /// {@return a deferred holder for the recipe serializer registered under the given key}
    ///
    /// @param key The registry key of the recipe serializer
    public static <T extends Recipe<?>> DeferredRecipeSerializer<T> createRecipeSerializer(ResourceKey<RecipeSerializer<?>> key) {
        return new DeferredRecipeSerializer<>(key);
    }
}
