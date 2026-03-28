package io.github.xfacthd.framedblocks.common.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class RecipeInputItemResourceHandler extends ItemStacksResourceHandler implements RecipeInput {
    public RecipeInputItemResourceHandler(int slots) {
        super(slots);
    }

    @Override
    public ItemStack getItem(int slot) {
        return getResource(slot).toStack(getAmountAsInt(slot));
    }
}
