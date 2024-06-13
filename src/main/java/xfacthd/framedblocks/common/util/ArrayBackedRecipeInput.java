package xfacthd.framedblocks.common.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ArrayBackedRecipeInput(ItemStack[] stacks) implements RecipeInput
{
    @Override
    public ItemStack getItem(int slot)
    {
        return stacks[slot];
    }

    @Override
    public int size()
    {
        return stacks.length;
    }
}
