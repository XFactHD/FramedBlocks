package xfacthd.framedblocks.client.screen;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IFramingSawScreen
{
    ItemStack getInputStack();

    ItemStack getAdditiveStack(int slot);

    Container getInputContainer();
}
