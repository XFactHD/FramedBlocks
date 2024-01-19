package xfacthd.framedblocks.common.datagen.builders.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class ExtShapelessRecipeBuilder extends ShapelessRecipeBuilder implements AutoUnlockNameBuilder<ExtShapelessRecipeBuilder>
{
    public ExtShapelessRecipeBuilder(RecipeCategory category, ItemLike result, int count)
    {
        super(category, result, count);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(TagKey<Item> pTag)
    {
        return (ExtShapelessRecipeBuilder) super.requires(pTag);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(ItemLike pItem)
    {
        return (ExtShapelessRecipeBuilder) super.requires(pItem);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(ItemLike pItem, int pQuantity)
    {
        return (ExtShapelessRecipeBuilder) super.requires(pItem, pQuantity);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(Ingredient pIngredient)
    {
        return (ExtShapelessRecipeBuilder) super.requires(pIngredient);
    }

    @Override
    public ExtShapelessRecipeBuilder requires(Ingredient pIngredient, int pQuantity)
    {
        return (ExtShapelessRecipeBuilder) super.requires(pIngredient, pQuantity);
    }

    @Override
    public ExtShapelessRecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion)
    {
        return (ExtShapelessRecipeBuilder) super.unlockedBy(pName, pCriterion);
    }

    @Override
    public ExtShapelessRecipeBuilder group(@Nullable String pGroupName)
    {
        return (ExtShapelessRecipeBuilder) super.group(pGroupName);
    }
}
