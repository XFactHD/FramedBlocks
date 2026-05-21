package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.client.screen.saw.FramingSawWithEncoderScreen;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.Optionull;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class FramingSawGhostIngredientHandler implements IGhostIngredientHandler<FramingSawWithEncoderScreen> {
    @Override
    @SuppressWarnings("unchecked")
    public <I> List<Target<I>> getTargetsTyped(FramingSawWithEncoderScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        return Optionull.mapOrDefault(getTarget(screen, ingredient), target -> List.of((Target<I>) target), List.of());
    }

    @Override
    public <I> boolean quickMove(FramingSawWithEncoderScreen screen, ITypedIngredient<I> ingredient) {
        InputTarget target = getTarget(screen, ingredient);
        if (target != null) {
            target.accept(ingredient.getItemStack().orElseThrow());
            return true;
        }
        return false;
    }

    private static <I> @Nullable InputTarget getTarget(FramingSawWithEncoderScreen screen, ITypedIngredient<I> ingredient) {
        if (screen.getMenu().isInEncoderMode() && ingredient.getType() == VanillaTypes.ITEM_STACK) {
            ItemStack stack = ingredient.getItemStack().orElseThrow();
            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0) {
                return new InputTarget(screen, FramingSawMenu.SLOT_INPUT);
            }

            RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getRecipes().get(screen.getMenu().getSelectedRecipeIndex()).toVanilla();
            List<FramingSawRecipeAdditive> additives = recipe.value().getAdditives();
            for (int i = 0; i < additives.size(); i++) {
                if (additives.get(i).ingredient().test(stack)) {
                    return new InputTarget(screen, i + 1);
                }
            }
        }
        return null;
    }

    @Override
    public void onComplete() { }

    private record InputTarget(FramingSawWithEncoderScreen screen, int slot, Rect2i area) implements Target<ItemStack> {
        public InputTarget(FramingSawWithEncoderScreen screen, int slot) {
            this(screen, slot, new Rect2i(screen.getInputSlotX() - 1, screen.getInputSlotY(slot) - 1, 18, 18));
        }

        @Override
        public Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(ItemStack stack) {
            screen.acceptEncodingInput(slot, stack);
        }
    }
}
