package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public final class PoweredFramingSawGuiContainerHandler implements IGuiContainerHandler<PoweredFramingSawScreen>
{
    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(PoweredFramingSawScreen screen, double mouseX, double mouseY)
    {
        int minX = PoweredFramingSawScreen.PROGRESS_X;
        int minY = PoweredFramingSawScreen.PROGRESS_Y;
        int maxX = minX + PoweredFramingSawScreen.PROGRESS_WIDTH;
        int maxY = minY + PoweredFramingSawScreen.PROGRESS_HEIGHT;
        if (mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY)
        {
            return List.of(new ClickableArea(new Rect2i(
                    minX, minY, PoweredFramingSawScreen.PROGRESS_WIDTH, PoweredFramingSawScreen.PROGRESS_HEIGHT
            )));
        }
        return List.of();
    }



    private record ClickableArea(Rect2i area) implements IGuiClickableArea
    {
        @Override
        public Rect2i getArea()
        {
            return area;
        }

        @Override
        public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui)
        {
            IIngredientManager ingredients = JeiCompat.GuardedAccess.getRuntime().getIngredientManager();
            Optional<ITypedIngredient<ItemStack>> optIng = ingredients.createTypedIngredient(
                    VanillaTypes.ITEM_STACK, new ItemStack(FBContent.BLOCK_POWERED_FRAMING_SAW.value())
            );
            optIng.ifPresent(ing -> recipesGui.show(focusFactory.createFocus(RecipeIngredientRole.CATALYST, ing)));
        }
    }
}
