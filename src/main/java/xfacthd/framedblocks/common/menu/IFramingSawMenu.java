package xfacthd.framedblocks.common.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public interface IFramingSawMenu
{
    ItemStack getInputStack();

    RecipeInput getRecipeInput();

    ItemStack getAdditiveStack(int slot);

    boolean isValidRecipeIndex(int idx);
}
