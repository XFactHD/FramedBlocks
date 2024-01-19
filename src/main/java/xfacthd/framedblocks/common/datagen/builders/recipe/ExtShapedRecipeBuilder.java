package xfacthd.framedblocks.common.datagen.builders.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class ExtShapedRecipeBuilder extends ShapedRecipeBuilder implements AutoUnlockNameBuilder<ExtShapedRecipeBuilder>
{
    public ExtShapedRecipeBuilder(RecipeCategory category, ItemLike result, int count)
    {
        super(category, result, count);
    }

    @Override
    public ExtShapedRecipeBuilder define(Character pSymbol, TagKey<Item> pTag)
    {
        return (ExtShapedRecipeBuilder) super.define(pSymbol, pTag);
    }

    @Override
    public ExtShapedRecipeBuilder define(Character pSymbol, ItemLike pItem)
    {
        return (ExtShapedRecipeBuilder) super.define(pSymbol, pItem);
    }

    @Override
    public ExtShapedRecipeBuilder define(Character pSymbol, Ingredient pIngredient)
    {
        return (ExtShapedRecipeBuilder) super.define(pSymbol, pIngredient);
    }

    @Override
    public ExtShapedRecipeBuilder pattern(String pPattern)
    {
        return (ExtShapedRecipeBuilder) super.pattern(pPattern);
    }

    @Override
    public ExtShapedRecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion)
    {
        return (ExtShapedRecipeBuilder) super.unlockedBy(pName, pCriterion);
    }

    @Override
    public ExtShapedRecipeBuilder group(@Nullable String pGroupName)
    {
        return (ExtShapedRecipeBuilder) super.group(pGroupName);
    }

    @Override
    public ExtShapedRecipeBuilder showNotification(boolean pShowNotification)
    {
        return (ExtShapedRecipeBuilder) super.showNotification(pShowNotification);
    }
}
