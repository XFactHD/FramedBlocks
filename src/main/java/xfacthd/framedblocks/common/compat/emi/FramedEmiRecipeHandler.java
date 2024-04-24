package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.*;
import xfacthd.framedblocks.common.net.payload.SelectFramingSawRecipePayload;

import java.util.ArrayList;
import java.util.List;

public final class FramedEmiRecipeHandler<T extends AbstractContainerMenu & IFramingSawMenu> implements StandardRecipeHandler<T>
{
    @Override
    public List<Slot> getInputSources(T menu)
    {
        List<Slot> list = new ArrayList<>(FramingSawMenu.TOTAL_SLOT_COUNT - 1);
        for (int i = 0; i < FramingSawMenu.SLOT_RESULT; i++)
        {
            list.add(menu.getSlot(i));
        }
        for (int i = FramingSawMenu.SLOT_INV_FIRST; i < FramingSawMenu.TOTAL_SLOT_COUNT; i++)
        {
            list.add(menu.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getCraftingSlots(T menu)
    {
        List<Slot> list = new ArrayList<>(FramingSawRecipe.MAX_ADDITIVE_COUNT + 1);
        for (int i = 0; i < FramingSawMenu.SLOT_RESULT; i++)
        {
            list.add(menu.getSlot(i));
        }
        return list;
    }

    @Override
    public Slot getOutputSlot(T menu)
    {
        return menu.getSlot(FramingSawMenu.SLOT_RESULT);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe.getCategory() == FramedEmiPlugin.SAW_CATEGORY;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context)
    {
        if (!(recipe instanceof FramingSawEmiRecipe sawRecipe) || !StandardRecipeHandler.super.craft(recipe, context))
        {
            return false;
        }

        int idx = FramingSawRecipeCache.get(true).getRecipes().indexOf(sawRecipe.getBackingRecipe());
        T menu = context.getScreenHandler();
        if (idx != -1 && menu.isValidRecipeIndex(idx))
        {
            //noinspection ConstantConditions
            if (menu.clickMenuButton(Minecraft.getInstance().player, idx))
            {
                PacketDistributor.sendToServer(new SelectFramingSawRecipePayload(menu.containerId, idx));
            }
        }
        return true;
    }
}
