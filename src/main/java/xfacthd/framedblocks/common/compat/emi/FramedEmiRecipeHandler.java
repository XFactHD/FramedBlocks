package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.ArrayList;
import java.util.List;

public final class FramedEmiRecipeHandler implements StandardRecipeHandler<FramingSawMenu>
{
    @Override
    public List<Slot> getInputSources(FramingSawMenu menu)
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
    public List<Slot> getCraftingSlots(FramingSawMenu menu)
    {
        List<Slot> list = new ArrayList<>(FramingSawRecipe.MAX_ADDITIVE_COUNT + 1);
        for (int i = 0; i < FramingSawMenu.SLOT_RESULT; i++)
        {
            list.add(menu.getSlot(i));
        }
        return list;
    }

    @Override
    public Slot getOutputSlot(FramingSawMenu menu)
    {
        return menu.getSlot(FramingSawMenu.SLOT_RESULT);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe.getCategory() == FramedEmiPlugin.SAW_CATEGORY;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<FramingSawMenu> context)
    {
        if (!(recipe instanceof FramingSawEmiRecipe sawRecipe) || !StandardRecipeHandler.super.craft(recipe, context))
        {
            return false;
        }

        int idx = FramingSawRecipeCache.get(true).getRecipes().indexOf(sawRecipe.getRecipe());
        FramingSawMenu menu = context.getScreenHandler();
        if (idx != -1 && menu.isValidRecipeIndex(idx))
        {
            //noinspection ConstantConditions
            if (menu.clickMenuButton(Minecraft.getInstance().player, idx))
            {
                //noinspection ConstantConditions
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, idx);
            }
        }
        return true;
    }
}
