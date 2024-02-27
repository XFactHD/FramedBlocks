package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import xfacthd.framedblocks.common.crafting.*;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.List;

public final class FramingSawGhostIngredientHandler implements IGhostIngredientHandler<FramingSawWithEncoderScreen>
{
    @Override
    @SuppressWarnings("unchecked")
    public <I> List<Target<I>> getTargetsTyped(FramingSawWithEncoderScreen screen, ITypedIngredient<I> ingredient, boolean doStart)
    {
        if (screen.getMenu().isInEncoderMode() && ingredient.getType() == VanillaTypes.ITEM_STACK)
        {
            ItemStack stack = ingredient.getItemStack().orElseThrow();
            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0)
            {
                return List.of((Target<I>) new InputTarget(screen, FramingSawMenu.SLOT_INPUT));
            }

            RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getRecipes().get(screen.getMenu().getSelectedRecipeIndex()).toVanilla();
            List<FramingSawRecipeAdditive> additives = recipe.value().getAdditives();
            for (int i = 0; i < additives.size(); i++)
            {
                if (additives.get(i).ingredient().test(stack))
                {
                    return List.of((Target<I>) new InputTarget(screen, i + 1));
                }
            }
        }
        return List.of();
    }

    @Override
    public void onComplete() { }



    private record InputTarget(FramingSawWithEncoderScreen screen, int slot, Rect2i area) implements Target<ItemStack>
    {
        public InputTarget(FramingSawWithEncoderScreen screen, int slot)
        {
            this(screen, slot, new Rect2i(screen.getInputSlotX() - 1, screen.getInputSlotY(slot) - 1, 18, 18));
        }

        @Override
        public Rect2i getArea()
        {
            return area;
        }

        @Override
        public void accept(ItemStack stack)
        {
            screen.acceptEncodingInput(slot, stack);
        }
    }
}
