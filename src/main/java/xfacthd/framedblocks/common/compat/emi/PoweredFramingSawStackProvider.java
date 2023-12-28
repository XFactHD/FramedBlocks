package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

public final class PoweredFramingSawStackProvider implements EmiStackProvider<PoweredFramingSawScreen>
{
    @Override
    public EmiStackInteraction getStackAt(PoweredFramingSawScreen screen, int x, int y)
    {
        RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getSelectedRecipe();
        if (screen.isMouseOverRecipeSlot(x, y) && recipe != null)
        {
            return new EmiStackInteraction(
                    EmiStack.of(recipe.value().getResult()),
                    EmiApi.getRecipeManager().getRecipe(recipe.id()),
                    false
            );
        }
        return EmiStackInteraction.EMPTY;
    }
}
