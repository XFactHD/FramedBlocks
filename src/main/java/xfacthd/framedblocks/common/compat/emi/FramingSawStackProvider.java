package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

public final class FramingSawStackProvider implements EmiStackProvider<FramingSawScreen>
{
    @Override
    public EmiStackInteraction getStackAt(FramingSawScreen screen, int x, int y)
    {
        FramingSawRecipe recipe = screen.getRecipeAt(x, y);
        if (recipe != null)
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
