package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.Optional;

public final class RecipeTransferErrorTransferNotImplemented implements IRecipeTransferError
{
    @Override
    public Type getType()
    {
        return Type.COSMETIC;
    }

    @Override
    public void showError(GuiGraphics graphics, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null)
        {
            graphics.renderTooltip(
                    Minecraft.getInstance().font,
                    List.of(JeiCompat.MSG_TRANSFER_NOT_IMPLEMENTED),
                    Optional.empty(),
                    mouseX,
                    mouseY
            );
        }
    }
}
