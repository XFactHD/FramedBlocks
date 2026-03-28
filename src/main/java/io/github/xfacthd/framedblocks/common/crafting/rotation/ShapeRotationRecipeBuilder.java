package io.github.xfacthd.framedblocks.common.crafting.rotation;

import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.ExtShapelessRecipeBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class ShapeRotationRecipeBuilder extends ExtShapelessRecipeBuilder {
    @Nullable
    private Ingredient tool = null;
    @Nullable
    private Ingredient block = null;

    public ShapeRotationRecipeBuilder(RecipeProvider provider, HolderGetter<Item> itemRegistry, ItemLike result) {
        super(provider, itemRegistry, RecipeCategory.BUILDING_BLOCKS, result, 1);
    }

    public ShapeRotationRecipeBuilder tool(Ingredient tool) {
        this.tool = tool;
        return this;
    }

    public ShapeRotationRecipeBuilder block(ItemLike block) {
        this.block = Ingredient.of(block);
        return this;
    }

    @Override
    public ShapeRotationRecipeBuilder requires(TagKey<Item> pTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShapeRotationRecipeBuilder requires(ItemLike pItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShapeRotationRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShapeRotationRecipeBuilder requires(Ingredient pIngredient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShapeRotationRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> key) {
        ShapeRotationRecipe recipe = new ShapeRotationRecipe(
                RecipeBuilder.createCraftingCommonInfo(true),
                RecipeBuilder.createCraftingBookInfo(category, group),
                result,
                Objects.requireNonNull(tool, "No tool specified"),
                Objects.requireNonNull(block, "No block specified")
        );
        output.accept(key, recipe, advancementBuilder.build(output, key, category));
    }
}
