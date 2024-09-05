package xfacthd.framedblocks.common.compat.jei.camo;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("removal")
public final class CamoCraftingRecipeExtension implements ICraftingCategoryExtension<JeiCamoApplicationRecipe>
{
    private final CamoCraftingHelper camoCraftingHelper;

    public CamoCraftingRecipeExtension(CamoCraftingHelper camoCraftingHelper)
    {
        this.camoCraftingHelper = camoCraftingHelper;
    }

    @Override
    public void setRecipe(RecipeHolder<JeiCamoApplicationRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses)
    {
        JeiCamoApplicationRecipe recipe = recipeHolder.value();
        camoCraftingHelper.setRecipe(recipe, builder, craftingGridHelper);
    }

    @Override
    public int getWidth(RecipeHolder<JeiCamoApplicationRecipe> recipeHolder)
    {
        return 2;
    }

    @Override
    public int getHeight(RecipeHolder<JeiCamoApplicationRecipe> recipeHolder)
    {
        return 2;
    }

    @Override
    public Optional<ResourceLocation> getRegistryName(RecipeHolder<JeiCamoApplicationRecipe> recipeHolder)
    {
        return Optional.of(recipeHolder.id());
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<JeiCamoApplicationRecipe> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses)
    {
        // The combinations of outputs for these recipes is way too much to calculate ahead of time.
        // If the focus is on an output it will already be set, but otherwise we need to calculate it here.

        if (recipeHolder.value().getResults().isEmpty())
        {
            IRecipeSlotDrawable frameSlot = recipeSlots.getFirst();
            IRecipeSlotDrawable inputOneSlot = recipeSlots.get(3);
            IRecipeSlotDrawable inputTwoSlot = recipeSlots.get(4);

            ItemStack output = camoCraftingHelper.calculateOutput(
                    frameSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY),
                    inputOneSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY),
                    inputTwoSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY)
            );

            recipeSlots.stream()
                    .filter(slot -> slot.getRole().equals(RecipeIngredientRole.OUTPUT))
                    .findAny()
                    .orElseThrow()
                    .createDisplayOverrides()
                    .addItemStack(output);
        }
    }
}
