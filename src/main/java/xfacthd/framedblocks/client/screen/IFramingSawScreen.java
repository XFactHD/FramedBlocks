package xfacthd.framedblocks.client.screen;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public interface IFramingSawScreen
{
    ItemStack getInputStack();

    ItemStack getAdditiveStack(int slot);

    RecipeInput getRecipeInput();
}
