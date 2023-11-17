package xfacthd.framedblocks.common.compat.jei;
/*
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
    private final IRecipeTransferInfo<FramingSawMenu, FramingSawRecipe> transferInfo;
    private final IRecipeTransferHandler<FramingSawMenu, FramingSawRecipe> wrappedHandler;

    public FramingSawTransferHandler(IRecipeTransferHandlerHelper transferHelper)
    {
        this.transferHelper = transferHelper;
        this.transferInfo = transferHelper.createBasicRecipeTransferInfo(
                FramingSawMenu.class,
                FBContent.MENU_TYPE_FRAMING_SAW.get(),
                FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE,
                FramingSawMenu.SLOT_INPUT,
                FramingSawMenu.SLOT_RESULT,
                FramingSawMenu.SLOT_INV_FIRST,
                FramingSawMenu.INV_SLOT_COUNT
        );
        this.wrappedHandler = transferHelper.createUnregisteredRecipeTransferHandler(transferInfo);
    }

    @Override
    public Class<? extends FramingSawMenu> getContainerClass()
    {
        return transferInfo.getContainerClass();
    }

    @Override
    public Optional<MenuType<FramingSawMenu>> getMenuType()
    {
        return transferInfo.getMenuType();
    }

    @Override
    public RecipeType<FramingSawRecipe> getRecipeType()
    {
        return transferInfo.getRecipeType();
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(FramingSawMenu menu, FramingSawRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer)
    {
        int idx = FramingSawRecipeCache.get(true).getRecipes().indexOf(recipe);
        if (idx != -1 && menu.isValidRecipeIndex(idx))
        {
            //TODO: https://github.com/mezz/JustEnoughItems/issues/3146
            //IRecipeTransferError error = wrappedHandler.transferRecipe(menu, recipe, recipeSlots, player, maxTransfer, doTransfer);
            //if (error != null)
            //{
            //    return error;
            //}

            if (doTransfer && menu.clickMenuButton(player, idx))
            {
                //noinspection ConstantConditions
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, idx);
            }
            // TODO: return null instead of "transfer not implemented" when the suggestion is implemented
            return new RecipeTransferErrorTransferNotImplemented();
        }
        return transferHelper.createUserErrorWithTooltip(JeiCompat.MSG_INVALID_RECIPE);
    }
}*/
