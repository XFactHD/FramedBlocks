package xfacthd.framedblocks.common.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import xfacthd.framedblocks.api.util.Utils;

import java.lang.invoke.MethodHandle;

public final class RecipeUtils
{
    private static final MethodHandle INGREDIENT_GET_VALUES = Utils.unreflectFieldGetter(Ingredient.class, "values");
    private static final MethodHandle INGREDIENT_TAGVALUE_GET_TAG = Utils.unreflectFieldGetter(Ingredient.TagValue.class, "tag");

    public static Ingredient.Value getSingleIngredientValue(Ingredient ing)
    {
        Ingredient.Value[] values;
        try
        {
            values = (Ingredient.Value[]) INGREDIENT_GET_VALUES.invoke(ing);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
        return values.length == 1 ? values[0] : null;
    }

    @SuppressWarnings("unchecked")
    public static TagKey<Item> getItemTagFromValue(Ingredient.TagValue value)
    {
        try
        {
            return (TagKey<Item>) INGREDIENT_TAGVALUE_GET_TAG.invoke(value);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }



    private RecipeUtils() { }
}
