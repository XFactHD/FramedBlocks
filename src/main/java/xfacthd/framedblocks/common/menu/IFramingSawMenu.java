package xfacthd.framedblocks.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IFramingSawMenu
{
    ItemStack getInputStack();

    Container getInputContainer();

    ItemStack getAdditiveStack(int slot);

    boolean isValidRecipeIndex(int idx);
}
