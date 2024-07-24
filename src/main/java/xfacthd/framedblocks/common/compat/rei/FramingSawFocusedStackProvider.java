package xfacthd.framedblocks.common.compat.rei;

import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.registry.screen.FocusedStackProvider;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

public final class FramingSawFocusedStackProvider implements FocusedStackProvider
{
    @Override
    public CompoundEventResult<EntryStack<?>> provide(Screen screen, Point mouse)
    {
        if (screen instanceof FramingSawScreen sawScreen)
        {
            FramingSawScreen.PointedRecipe recipe = sawScreen.getRecipeAt(mouse.x, mouse.y);
            if (recipe != null)
            {
                return CompoundEventResult.interruptTrue(EntryStacks.of(recipe.recipe().getResult()));
            }
        }
        else if (screen instanceof PoweredFramingSawScreen sawScreen)
        {
            RecipeHolder<FramingSawRecipe> recipe = sawScreen.getMenu().getSelectedRecipe();
            if (sawScreen.isMouseOverRecipeSlot(mouse.x, mouse.y) && recipe != null)
            {
                return CompoundEventResult.interruptTrue(EntryStacks.of(recipe.value().getResult()));
            }
        }
        return CompoundEventResult.pass();
    }
}
