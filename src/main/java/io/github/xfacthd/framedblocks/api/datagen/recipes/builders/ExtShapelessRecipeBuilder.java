package io.github.xfacthd.framedblocks.api.datagen.recipes.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public class ExtShapelessRecipeBuilder extends ShapelessRecipeBuilder implements AutoUnlockNameBuilder<ExtShapelessRecipeBuilder> {
    private final RecipeProvider provider;

    public ExtShapelessRecipeBuilder(RecipeProvider provider, HolderGetter<Item> itemRegistry, RecipeCategory category, ItemLike result, int count) {
        super(itemRegistry, category, new ItemStackTemplate(result.asItem(), count));
        this.provider = provider;
    }

    @Override
    public ExtShapelessRecipeBuilder requires(TagKey<Item> pTag) {
        return (ExtShapelessRecipeBuilder) super.requires(pTag);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(ItemLike pItem) {
        return (ExtShapelessRecipeBuilder) super.requires(pItem);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        return (ExtShapelessRecipeBuilder) super.requires(pItem, pQuantity);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(Ingredient pIngredient) {
        return (ExtShapelessRecipeBuilder) super.requires(pIngredient);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
        return (ExtShapelessRecipeBuilder) super.requires(pIngredient, pQuantity);
    }

    @Override
    public ExtShapelessRecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return (ExtShapelessRecipeBuilder) super.unlockedBy(pName, pCriterion);
    }

    @Override public ExtShapelessRecipeBuilder group(@Nullable String pGroupName) {
        return (ExtShapelessRecipeBuilder) super.group(pGroupName);
    }

    @Override
    @ApiStatus.Internal
    public final RecipeProvider provider() {
        return provider;
    }
}
