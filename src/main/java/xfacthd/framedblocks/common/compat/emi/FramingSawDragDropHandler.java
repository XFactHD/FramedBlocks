package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import xfacthd.framedblocks.common.crafting.*;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.List;

public final class FramingSawDragDropHandler implements EmiDragDropHandler<FramingSawWithEncoderScreen>
{
    @Override
    public void render(FramingSawWithEncoderScreen screen, EmiIngredient dragged, GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        if (isFramedBlock(dragged))
        {
            int x = screen.getInputSlotX() - 1;
            int y = screen.getInputSlotY(FramingSawMenu.SLOT_INPUT) - 1;
            graphics.fill(x, y, x + 18, y + 18, 0x8822BB33);
        }
        else if (dragged instanceof EmiStack emiStack)
        {
            ItemStack stack = emiStack.getItemStack();
            RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getRecipes().get(screen.getMenu().getSelectedRecipeIndex()).toVanilla();
            List<FramingSawRecipeAdditive> additives = recipe.value().getAdditives();
            for (int i = 0; i < additives.size(); i++)
            {
                if (additives.get(i).ingredient().test(stack))
                {
                    int x = screen.getInputSlotX() - 1;
                    int y = screen.getInputSlotY(i + 1) - 1;
                    graphics.fill(x, y, x + 18, y + 18, 0x8822BB33);
                    break;
                }
            }
        }
    }

    @Override
    public boolean dropStack(FramingSawWithEncoderScreen screen, EmiIngredient ingredient, int dropX, int dropY)
    {
        if (isFramedBlock(ingredient))
        {
            int x = screen.getInputSlotX();
            int y = screen.getInputSlotY(FramingSawMenu.SLOT_INPUT);
            if (dropX >= x && dropX < x + 16 && dropY >= y && dropY < y + 16)
            {
                screen.acceptEncodingInput(FramingSawMenu.SLOT_INPUT, ((EmiStack) ingredient).getItemStack());
                return true;
            }
        }
        else if (ingredient instanceof EmiStack emiStack)
        {
            ItemStack stack = emiStack.getItemStack();
            RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getRecipes().get(screen.getMenu().getSelectedRecipeIndex()).toVanilla();
            List<FramingSawRecipeAdditive> additives = recipe.value().getAdditives();
            for (int i = 0; i < additives.size(); i++)
            {
                int x = screen.getInputSlotX();
                int y = screen.getInputSlotY(i + 1);
                if (dropX >= x && dropX < x + 16 && dropY >= y && dropY < y + 16)
                {
                    if (additives.get(i).ingredient().test(stack))
                    {
                        screen.acceptEncodingInput(i + 1, emiStack.getItemStack());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isFramedBlock(EmiIngredient ingredient)
    {
        if (ingredient instanceof EmiStack emiStack)
        {
            Item stack = emiStack.getItemStack().getItem();
            return FramingSawRecipeCache.get(true).getMaterialValue(stack) > 0;
        }
        return false;
    }
}
