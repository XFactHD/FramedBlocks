package io.github.xfacthd.framedblocks.api.datagen.recipes;

import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.ExtShapedRecipeBuilder;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.ExtShapelessRecipeBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

/// Base recipe provider implementation providing factory methods for extended crafting table recipe builders.
public abstract class AbstractFramedRecipeProvider extends RecipeProvider {
    protected AbstractFramedRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
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
        return new ExtShapedRecipeBuilder(this, items, category, output, count);
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
        return new ExtShapelessRecipeBuilder(this, items, category, output, count);
    }
}
