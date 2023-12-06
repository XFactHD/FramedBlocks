package xfacthd.framedblocks.common.crafting;

import com.google.common.base.Preconditions;
import net.minecraft.advancements.*;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class FramingSawRecipeBuilder implements RecipeBuilder
{
    private final Item result;
    private final int count;
    private int material = 0;
    private List<FramingSawRecipeAdditive> additives = List.of();
    private boolean disabled = false;

    private FramingSawRecipeBuilder(ItemLike result, int count)
    {
        this.result = result.asItem();
        this.count = count;
    }

    public static <T extends ItemLike> FramingSawRecipeBuilder builder(Holder<T> result)
    {
        return builder(result.value());
    }

    public static FramingSawRecipeBuilder builder(ItemLike result)
    {
        return builder(result, 1);
    }

    public static <T extends ItemLike> FramingSawRecipeBuilder builder(Holder<T> result, int count)
    {
        return builder(result.value(), count);
    }

    public static FramingSawRecipeBuilder builder(ItemLike result, int count)
    {
        Preconditions.checkNotNull(result, "Result must be non-null");
        Preconditions.checkArgument(count > 0, "Result count must be greater than 0");
        return new FramingSawRecipeBuilder(result, count);
    }

    public FramingSawRecipeBuilder material(int material)
    {
        Preconditions.checkArgument(material > 0, "Material value must be greater than 0");
        this.material = material;
        return this;
    }

    public FramingSawRecipeBuilder additive(FramingSawRecipeAdditive additive)
    {
        Preconditions.checkNotNull(additive, "Additive must be non-null");
        this.additives = List.of(additive);
        return this;
    }

    public FramingSawRecipeBuilder additives(List<FramingSawRecipeAdditive> additives)
    {
        Preconditions.checkNotNull(additives, "Additives must be non-null");
        this.additives = additives;
        return this;
    }

    public FramingSawRecipeBuilder disabled()
    {
        this.disabled = true;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, Criterion<?> criterion)
    {
        throw new UnsupportedOperationException("Advancements are not supported");
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName)
    {
        throw new UnsupportedOperationException("Recipe groups are not supported");
    }

    @Override
    public Item getResult()
    {
        return result;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation recipeId)
    {
        Preconditions.checkState(material > 0, "Material value not set");
        Preconditions.checkState(material / count * count == material, "Material value not divisible by result size");

        recipeId = recipeId.withPrefix("framing_saw/");
        FramingSawRecipe recipe = new FramingSawRecipe(material, additives, new ItemStack(result, count), disabled);
        output.accept(recipeId, recipe, null);
    }
}
