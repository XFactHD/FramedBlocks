package xfacthd.framedblocks.common.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.util.Utils;

public final class FramingSawRecipeCalculation
{
    private final FramingSawRecipe recipe;
    private final int inputValue;
    private final long lcm;

    FramingSawRecipeCalculation(FramingSawRecipe recipe, Container container, boolean client)
    {
        this.recipe = recipe;
        this.inputValue = getInputValue(container.getItem(0), client);
        this.lcm = getMaterialLCM(recipe, inputValue);
    }

    public int getInputCount()
    {
        return (int) (lcm / inputValue);
    }

    public int getOutputCount()
    {
        return getOutputCount(recipe.getMaterialAmount(), recipe.getResult(), lcm);
    }

    public int getAdditiveCount(int idx)
    {
        return getAdditiveCount(recipe, recipe.getAdditives().get(idx), lcm);
    }



    static int getInputValue(ItemStack input, boolean client)
    {
        FramingSawRecipeCache cache = FramingSawRecipeCache.get(client);
        return cache.getMaterialValue(input.getItem());
    }

    static long getMaterialLCM(FramingSawRecipe recipe, int inputValue)
    {
        return Utils.lcm(inputValue, recipe.getMaterialAmount());
    }

    static int getOutputCount(int materialAmount, ItemStack result, long lcm)
    {
        return (int) (lcm / materialAmount * result.getCount());
    }

    static int getAdditiveCount(FramingSawRecipe recipe, FramingSawRecipeAdditive additive, long lcm)
    {
        return (int) (lcm / recipe.getMaterialAmount()) * additive.count();
    }
}
