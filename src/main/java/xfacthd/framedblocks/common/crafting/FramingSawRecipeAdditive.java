package xfacthd.framedblocks.common.crafting;

import com.google.common.base.Preconditions;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record FramingSawRecipeAdditive(Ingredient ingredient, int count)
{
    public FramingSawRecipeAdditive
    {
        Preconditions.checkArgument(ingredient != null, "Additive ingredient must be non-null");
        Preconditions.checkArgument(count > 0, "Additive count must be greater than 0");
    }

    public static FramingSawRecipeAdditive of(TagKey<Item> tag)
    {
        return of(tag, 1);
    }

    public static FramingSawRecipeAdditive of(TagKey<Item> tag, int count)
    {
        return new FramingSawRecipeAdditive(Ingredient.of(tag), count);
    }

    public static FramingSawRecipeAdditive of(ItemLike item)
    {
        return of(item, 1);
    }

    public static FramingSawRecipeAdditive of(ItemLike item, int count)
    {
        return new FramingSawRecipeAdditive(Ingredient.of(item), count);
    }
}
