// FIXME: RL->ID
/*package io.github.xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import io.github.xfacthd.framedblocks.client.screen.saw.PoweredFramingSawScreen;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class PoweredFramingSawStackProvider implements EmiStackProvider<PoweredFramingSawScreen>{
    @Override
    public EmiStackInteraction getStackAt(PoweredFramingSawScreen screen, int x, int y) {
        RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getSelectedRecipe();
        if (screen.isMouseOverRecipeSlot(x, y) && recipe != null) {
            return new EmiStackInteraction(
                    EmiStack.of(recipe.value().getResult()),
                    EmiApi.getRecipeManager().getRecipe(recipe.id().identifier()),
                    false
            );
        }
        return EmiStackInteraction.EMPTY;
    }
}
*/