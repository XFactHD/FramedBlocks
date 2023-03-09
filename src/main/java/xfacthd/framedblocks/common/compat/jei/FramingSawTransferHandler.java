package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

public final class FramingSawTransferHandler implements IRecipeTransferHandler<FramingSawMenu, FramingSawRecipe>
{
    public static final Component MSG_INVALID_RECIPE = Utils.translate("msg", "framing_saw.transfer.invalid_recipe");

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
    public Class<FramingSawRecipe> getRecipeClass()
    {
        return FramingSawRecipe.class;
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
        return transferHelper.createUserErrorWithTooltip(MSG_INVALID_RECIPE);
    }
}
