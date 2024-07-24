package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import xfacthd.framedblocks.client.screen.FramingSawScreen;

import java.util.Optional;

public sealed class FramingSawGuiContainerHandler<T extends FramingSawScreen> implements IGuiContainerHandler<T> permits FramingSawWithEncoderGuiContainerHandler
{
    private final IIngredientManager ingredientManager;

    public FramingSawGuiContainerHandler(IIngredientManager ingredientManager)
    {
        this.ingredientManager = ingredientManager;
    }

    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T screen, double mouseX, double mouseY)
    {
        FramingSawScreen.PointedRecipe recipe = screen.getRecipeAt(mouseX, mouseY);
        if (recipe != null)
        {
            return ingredientManager.createTypedIngredient(recipe.recipe().getResult())
                    .map(stack -> new ClickableStack(stack, recipe.area()));
        }
        return Optional.empty();
    }
}
