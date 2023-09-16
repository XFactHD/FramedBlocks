package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

public class PoweredFramingSawStackProvider implements EmiStackProvider<PoweredFramingSawScreen>
{
    @Override
    public EmiStackInteraction getStackAt(PoweredFramingSawScreen screen, int x, int y)
    {
        FramingSawRecipe recipe = screen.getMenu().getSelectedRecipe();
        if (screen.isMouseOverRecipeSlot(x, y) && recipe != null)
        {
            return new EmiStackInteraction(
                    EmiStack.of(recipe.getResult()),
                    EmiApi.getRecipeManager().getRecipe(recipe.getId()),
                    false
            );
        }
        return EmiStackInteraction.EMPTY;
    }
}
