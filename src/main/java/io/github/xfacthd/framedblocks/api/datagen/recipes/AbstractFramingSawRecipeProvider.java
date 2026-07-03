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

/// Base recipe provider implementation providing factory methods for Framing Saw recipe builders.
public abstract class AbstractFramingSawRecipeProvider extends RecipeProvider {
    /// The material value of a single Framed Cube.
    public static final int CUBE_MATERIAL_VALUE = 6144; // Empirically determined value

    protected AbstractFramingSawRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    /// {@return a framing saw recipe builder with one of the given result}
    ///
    /// @param result The recipe result
    protected static <T extends ItemLike> FramingSawRecipeBuilder sawRecipe(Holder<T> result) {
        return sawRecipe(result.value());
    }

    /// {@return a framing saw recipe builder with one of the given result}
    ///
    /// @param result The recipe result
    protected static FramingSawRecipeBuilder sawRecipe(ItemLike result) {
        return sawRecipe(result, 1);
    }

    /// {@return a framing saw recipe builder with the given amount of the given result}
    ///
    /// @param result The recipe result
    /// @param count  The amount of the recipe result
    protected static <T extends ItemLike> FramingSawRecipeBuilder sawRecipe(Holder<T> result, int count) {
        return sawRecipe(result.value(), count);
    }

    /// {@return a framing saw recipe builder with the given amount of the given result}
    ///
    /// @param result The recipe result
    /// @param count  The amount of the recipe result
    protected static FramingSawRecipeBuilder sawRecipe(ItemLike result, int count) {
        return new FramingSawRecipeBuilder(result, count);
    }

    /// {@return a framing saw additive consuming one of the given item tag}
    ///
    /// @param tag   The item tag to consume
    protected final FramingSawRecipeBuilder.Additive additive(TagKey<Item> tag) {
        return FramingSawRecipeBuilder.Additive.of(tag(tag), 1);
    }

    /// {@return a framing saw additive consuming the given amount of the given item tag}
    ///
    /// @param tag   The item tag to consume
    /// @param count The amount to consume
    protected final FramingSawRecipeBuilder.Additive additive(TagKey<Item> tag, int count) {
        return FramingSawRecipeBuilder.Additive.of(tag(tag), count);
    }

    /// {@return a framing saw additive consuming one of the given item}
    ///
    /// @param item  The item to consume
    protected static FramingSawRecipeBuilder.Additive additive(ItemLike item) {
        return FramingSawRecipeBuilder.Additive.of(item, 1);
    }

    /// {@return a framing saw additive consuming the given amount of the given item}
    ///
    /// @param item  The item to consume
    /// @param count The amount to consume
    protected static FramingSawRecipeBuilder.Additive additive(ItemLike item, int count) {
        return FramingSawRecipeBuilder.Additive.of(Ingredient.of(item), count);
    }
}
