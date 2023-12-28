package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

public final class FramingSawStackProvider implements EmiStackProvider<FramingSawScreen>
{
    @Override
    public EmiStackInteraction getStackAt(FramingSawScreen screen, int x, int y)
    {
        RecipeHolder<FramingSawRecipe> recipe = screen.getRecipeAt(x, y);
        if (recipe != null)
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
