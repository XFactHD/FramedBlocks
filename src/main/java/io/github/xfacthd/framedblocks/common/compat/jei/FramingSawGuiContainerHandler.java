package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.client.screen.FramingSawScreen;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public sealed class FramingSawGuiContainerHandler<T extends FramingSawScreen> implements IGuiContainerHandler<T> permits FramingSawWithEncoderGuiContainerHandler
{
    @Override
    public Optional<IClickableIngredient<ItemStack>> getClickableIngredientUnderMouse(
            IClickableIngredientFactory factory, T screen, double mouseX, double mouseY
    )
    {
        FramingSawScreen.PointedRecipe recipe = screen.getRecipeAt(mouseX, mouseY);
        if (recipe != null)
        {
            return factory.createBuilder(recipe.recipe().getResultStack()).buildWithArea(recipe.area());
        }
        return Optional.empty();
    }
}
