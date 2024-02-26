package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

public final class FramingSawStackProvider implements EmiStackProvider<FramingSawScreen>
{
    @Override
    public EmiStackInteraction getStackAt(FramingSawScreen screen, int x, int y)
    {
        FramingSawMenu.FramedRecipeHolder recipe = screen.getRecipeAt(x, y);
        if (recipe != null)
        {
            return new EmiStackInteraction(
                    EmiStack.of(recipe.getRecipe().getResult()),
                    EmiApi.getRecipeManager().getRecipe(recipe.toVanilla().id()),
                    false
            );
        }
        return EmiStackInteraction.EMPTY;
    }
}
