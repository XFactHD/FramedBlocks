package xfacthd.framedblocks.common.compat.jei;
/*
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

import java.util.List;

public final class PoweredFramingSawGhostIngredientHandler implements IGhostIngredientHandler<PoweredFramingSawScreen>
{
    @Override
    @SuppressWarnings("unchecked")
    public <I> List<Target<I>> getTargetsTyped(PoweredFramingSawScreen screen, ITypedIngredient<I> ingredient, boolean doStart)
    {
        if (ingredient.getType() == VanillaTypes.ITEM_STACK)
        {
            ItemStack stack = ingredient.getItemStack().orElseThrow();
            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0)
            {
                return List.of((Target<I>) new SawTarget(screen));
            }
        }
        return List.of();
    }

    @Override
    public void onComplete() { }



    private record SawTarget(PoweredFramingSawScreen screen, Rect2i area) implements Target<ItemStack>
    {
        private SawTarget(PoweredFramingSawScreen screen)
        {
            this(screen, new Rect2i(screen.getTargetStackX() - 1, screen.getTargetStackY() - 1, 18, 18));
        }

        @Override
        public Rect2i getArea()
        {
            return area;
        }

        @Override
        public void accept(ItemStack stack)
        {
            screen.selectRecipe(stack);
        }
    }
}*/
