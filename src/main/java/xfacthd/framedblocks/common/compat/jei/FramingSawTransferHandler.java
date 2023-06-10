package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.Optional;

public final class FramingSawTransferHandler implements IRecipeTransferHandler<FramingSawMenu, FramingSawRecipe>
{
    private final IRecipeTransferHandlerHelper transferHelper;

    public FramingSawTransferHandler(IRecipeTransferHandlerHelper transferHelper)
    {
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<FramingSawMenu> getContainerClass()
    {
        return FramingSawMenu.class;
    }

    @Override
    public Optional<MenuType<FramingSawMenu>> getMenuType()
    {
        return Optional.of(FBContent.menuTypeFramingSaw.get());
    }

    @Override
    public RecipeType<FramingSawRecipe> getRecipeType()
    {
        return FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE;
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(FramingSawMenu menu, FramingSawRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer)
    {
        int idx = FramingSawRecipeCache.get(true).getRecipes().indexOf(recipe);
        if (idx != -1 && menu.isValidRecipeIndex(idx))
        {
            //TODO: https://github.com/mezz/JustEnoughItems/issues/3146
            if (doTransfer && menu.clickMenuButton(player, idx))
            {
                //noinspection ConstantConditions
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, idx);
            }
            return new RecipeTransferErrorTransferNotImplemented();
        }
        return transferHelper.createUserErrorWithTooltip(JeiCompat.MSG_INVALID_RECIPE);
    }
}
