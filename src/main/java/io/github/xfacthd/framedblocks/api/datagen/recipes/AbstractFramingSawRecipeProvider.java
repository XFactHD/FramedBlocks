package io.github.xfacthd.framedblocks.api.datagen.recipes;

import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.FramingSawRecipeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public abstract class AbstractFramingSawRecipeProvider extends RecipeProvider {
    public static final int CUBE_MATERIAL_VALUE = 6144; // Empirically determined value

    protected AbstractFramingSawRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    protected static <T extends ItemLike> FramingSawRecipeBuilder sawRecipe(Holder<T> result) {
        return sawRecipe(result.value());
    }

    protected static FramingSawRecipeBuilder sawRecipe(ItemLike result) {
        return sawRecipe(result, 1);
    }

    protected static <T extends ItemLike> FramingSawRecipeBuilder sawRecipe(Holder<T> result, int count) {
        return sawRecipe(result.value(), count);
    }

    protected static FramingSawRecipeBuilder sawRecipe(ItemLike result, int count) {
        return new FramingSawRecipeBuilder(result, count);
    }

    protected final FramingSawRecipeBuilder.Additive additive(TagKey<Item> tag) {
        return FramingSawRecipeBuilder.Additive.of(tag(tag), 1);
    }

    protected final FramingSawRecipeBuilder.Additive additive(TagKey<Item> tag, int count) {
        return FramingSawRecipeBuilder.Additive.of(tag(tag), count);
    }

    protected static FramingSawRecipeBuilder.Additive additive(ItemLike item) {
        return FramingSawRecipeBuilder.Additive.of(item, 1);
    }

    protected static FramingSawRecipeBuilder.Additive additive(ItemLike item, int count) {
        return FramingSawRecipeBuilder.Additive.of(Ingredient.of(item), count);
    }
}
