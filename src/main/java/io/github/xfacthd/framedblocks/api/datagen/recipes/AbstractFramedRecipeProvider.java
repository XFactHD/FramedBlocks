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

public abstract class AbstractFramedRecipeProvider extends RecipeProvider {
    protected final HolderGetter<Item> itemRegistry;

    protected AbstractFramedRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.itemRegistry = registries.lookupOrThrow(Registries.ITEM);
    }

    protected final ExtShapedRecipeBuilder shapedBuildingBlock(ItemLike output) {
        return shapedBuildingBlock(output, 1);
    }

    protected final ExtShapedRecipeBuilder shapedBuildingBlock(ItemLike output, int count) {
        return shapedRecipe(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    protected final ExtShapedRecipeBuilder shapedRecipe(RecipeCategory category, ItemLike output) {
        return shapedRecipe(category, output, 1);
    }

    protected final ExtShapedRecipeBuilder shapedRecipe(RecipeCategory category, ItemLike output, int count) {
        return new ExtShapedRecipeBuilder(this, itemRegistry, category, output, count);
    }

    protected final ExtShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output) {
        return shapelessBuildingBlock(output, 1);
    }

    protected final ExtShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output, int count) {
        return shapelessRecipe(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    protected final ExtShapelessRecipeBuilder shapelessRecipe(RecipeCategory category, ItemLike output) {
        return shapelessRecipe(category, output, 1);
    }

    protected final ExtShapelessRecipeBuilder shapelessRecipe(RecipeCategory category, ItemLike output, int count) {
        return new ExtShapelessRecipeBuilder(this, itemRegistry, category, output, count);
    }
}
