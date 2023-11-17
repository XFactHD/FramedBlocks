package xfacthd.framedblocks.common.compat.emi;
/*
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

public final class PoweredFramingSawDragDropHandler implements EmiDragDropHandler<PoweredFramingSawScreen>
{
    @Override
    public void render(PoweredFramingSawScreen screen, EmiIngredient dragged, GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        if (isFramedBlock(dragged))
        {
            int x = screen.getTargetStackX() - 1;
            int y = screen.getTargetStackY() - 1;
            graphics.fill(x, y, x + 18, y + 18, 0x8822BB33);
        }
    }

    @Override
    public boolean dropStack(PoweredFramingSawScreen screen, EmiIngredient stack, int dropX, int dropY)
    {
        if (!isFramedBlock(stack))
        {
            return false;
        }

        int x = screen.getTargetStackX();
        int y = screen.getTargetStackY();
        if (dropX >= x && dropX < x + 16 && dropY >= y && dropY < y + 16)
        {
            screen.selectRecipe(stack.getEmiStacks().get(0).getItemStack());
            return true;
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
}*/
