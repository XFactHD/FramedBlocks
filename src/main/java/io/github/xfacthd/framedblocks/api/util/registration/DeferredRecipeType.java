package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredRecipeType<T extends Recipe<?>> extends DeferredHolder<RecipeType<?>, RecipeType<T>> {
    private DeferredRecipeType(ResourceKey<RecipeType<?>> key) {
        super(key);
    }

    public static <T extends Recipe<?>> DeferredRecipeType<T> createRecipeType(Identifier name) {
        return createRecipeType(ResourceKey.create(Registries.RECIPE_TYPE, name));
    }

    public static <T extends Recipe<?>> DeferredRecipeType<T> createRecipeType(ResourceKey<RecipeType<?>> key) {
        return new DeferredRecipeType<>(key);
    }
}
