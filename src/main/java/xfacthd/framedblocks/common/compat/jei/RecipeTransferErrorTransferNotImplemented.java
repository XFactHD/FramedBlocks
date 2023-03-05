package xfacthd.framedblocks.common.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Optional;

public final class RecipeTransferErrorTransferNotImplemented implements IRecipeTransferError
{
    public static final Component MSG_TRANSFER_NOT_IMPLEMENTED = Utils.translate("msg", "framing_saw.transfer.not_implemented");

    @Override
    public Type getType()
    {
        return Type.COSMETIC;
    }

    @Override
    public int getButtonHighlightColor()
    {
        return 0x80FFA500;
    }

    @Override
    public void showError(PoseStack poseStack, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null)
        {
            screen.renderTooltip(poseStack, List.of(MSG_TRANSFER_NOT_IMPLEMENTED), Optional.empty(), mouseX, mouseY);
        }
    }
}
