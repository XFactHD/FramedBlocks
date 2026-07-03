package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deferred holder for [RecipeType]s.
public final class DeferredRecipeType<T extends Recipe<?>> extends DeferredHolder<RecipeType<?>, RecipeType<T>> {
    private DeferredRecipeType(ResourceKey<RecipeType<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the recipe type registered under the given name}
    ///
    /// @param name The registry name of the recipe type
    public static <T extends Recipe<?>> DeferredRecipeType<T> createRecipeType(Identifier name) {
        return createRecipeType(ResourceKey.create(Registries.RECIPE_TYPE, name));
    }

    /// {@return a deferred holder for the recipe type registered under the given key}
    ///
    /// @param key The registry key of the recipe type
    public static <T extends Recipe<?>> DeferredRecipeType<T> createRecipeType(ResourceKey<RecipeType<?>> key) {
        return new DeferredRecipeType<>(key);
    }
}
