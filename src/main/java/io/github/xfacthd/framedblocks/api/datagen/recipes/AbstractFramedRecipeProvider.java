package io.github.xfacthd.framedblocks.api.datagen.recipes;

import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.ExtShapedRecipeBuilder;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.ExtShapelessRecipeBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/// Base recipe provider implementation providing factory methods for extended crafting table recipe builders.
public abstract class AbstractFramedRecipeProvider extends RecipeProvider {
    protected final HolderGetter<Item> itemRegistry;

    protected AbstractFramedRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.itemRegistry = registries.lookupOrThrow(Registries.ITEM);
    }

    /// Creates a shaped recipe builder with one of the given output in the `building blocks` category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param output The recipe result
    /// @return a new shaped recipe builder
    protected final ExtShapedRecipeBuilder shapedBuildingBlock(ItemLike output) {
        return shapedBuildingBlock(output, 1);
    }

    /// Creates a shaped recipe builder with the given amount of the given output in the `building blocks` category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param output The recipe result
    /// @param count  The amount of the recipe result
    /// @return a new shaped recipe builder
    protected final ExtShapedRecipeBuilder shapedBuildingBlock(ItemLike output, int count) {
        return shapedRecipe(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    /// Creates a shaped recipe builder with one of the given output in the given category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param category The category of the recipe
    /// @param output   The recipe result
    /// @return a new shaped recipe builder
    protected final ExtShapedRecipeBuilder shapedRecipe(RecipeCategory category, ItemLike output) {
        return shapedRecipe(category, output, 1);
    }

    /// Creates a shaped recipe builder with the given amount of the given output in the given category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param category The category of the recipe
    /// @param output   The recipe result
    /// @param count    The amount of the recipe result
    /// @return a new shaped recipe builder
    protected final ExtShapedRecipeBuilder shapedRecipe(RecipeCategory category, ItemLike output, int count) {
        return new ExtShapedRecipeBuilder(this, itemRegistry, category, output, count);
    }

    /// Creates a shapeless recipe builder with one of the given output in the `building blocks` category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param output The recipe result
    /// @return a new shapeless recipe builder
    protected final ExtShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output) {
        return shapelessBuildingBlock(output, 1);
    }

    /// Creates a shapeless recipe builder with the given amount of the given output in the `building blocks` category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param output The recipe result
    /// @param count  The amount of the recipe result
    /// @return a new shapeless recipe builder
    protected final ExtShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output, int count) {
        return shapelessRecipe(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    /// Creates a shapeless recipe builder with one of the given output in the given category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param category The category of the recipe
    /// @param output   The recipe result
    /// @return a new shapeless recipe builder
    protected final ExtShapelessRecipeBuilder shapelessRecipe(RecipeCategory category, ItemLike output) {
        return shapelessRecipe(category, output, 1);
    }

    /// Creates a shapeless recipe builder with the given amount of the given output in the given category.
    /// The returned builder supports generating recipe advancements with auto-generated criterion names.
    ///
    /// @param category The category of the recipe
    /// @param output   The recipe result
    /// @param count    The amount of the recipe result
    /// @return a new shapeless recipe builder
    protected final ExtShapelessRecipeBuilder shapelessRecipe(RecipeCategory category, ItemLike output, int count) {
        return new ExtShapelessRecipeBuilder(this, itemRegistry, category, output, count);
    }
}
