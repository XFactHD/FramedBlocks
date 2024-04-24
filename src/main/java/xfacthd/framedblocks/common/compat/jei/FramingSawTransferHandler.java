package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.*;
import xfacthd.framedblocks.common.net.payload.SelectFramingSawRecipePayload;

import java.util.List;
import java.util.Optional;

public abstract sealed class FramingSawTransferHandler<C extends AbstractContainerMenu & IFramingSawMenu>
        implements IRecipeTransferHandler<C, FramingSawRecipe>
        permits FramingSawTransferHandler.FramingSaw, FramingSawTransferHandler.PoweredFramingSaw
{
    private final IRecipeTransferHandlerHelper transferHelper;
    private final IRecipeTransferInfo<C, FramingSawRecipe> transferInfo;
    private final IRecipeTransferHandler<C, FramingSawRecipe> wrappedHandler;

    private FramingSawTransferHandler(IRecipeTransferHandlerHelper transferHelper, Class<? extends C> menuClass, MenuType<C> menuType)
    {
        this.transferHelper = transferHelper;
        this.transferInfo = transferHelper.createBasicRecipeTransferInfo(
                menuClass,
                menuType,
                FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE,
                FramingSawMenu.SLOT_INPUT,
                FramingSawMenu.SLOT_RESULT,
                FramingSawMenu.SLOT_INV_FIRST,
                FramingSawMenu.INV_SLOT_COUNT
        );
        this.wrappedHandler = transferHelper.createUnregisteredRecipeTransferHandler(transferInfo);
    }

    @Override
    public Class<? extends C> getContainerClass()
    {
        return transferInfo.getContainerClass();
    }

    @Override
    public Optional<MenuType<C>> getMenuType()
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
    public IRecipeTransferError transferRecipe(C menu, FramingSawRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer)
    {
        int idx = -1;
        List<RecipeHolder<FramingSawRecipe>> recipes = FramingSawRecipeCache.get(true).getRecipes();
        for (int i = 0; i < recipes.size(); i++)
        {
            if (recipes.get(i).value() == recipe)
            {
                idx = i;
                break;
            }
        }
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
                PacketDistributor.sendToServer(new SelectFramingSawRecipePayload(menu.containerId, idx));
            }
            // TODO: return null instead of "transfer not implemented" when the suggestion is implemented
            return new RecipeTransferErrorTransferNotImplemented();
        }
        return transferHelper.createUserErrorWithTooltip(JeiCompat.MSG_INVALID_RECIPE);
    }



    public static final class FramingSaw extends FramingSawTransferHandler<FramingSawMenu>
    {
        public FramingSaw(IRecipeTransferHandlerHelper transferHelper)
        {
            super(transferHelper, FramingSawMenu.class, FBContent.MENU_TYPE_FRAMING_SAW.get());
        }
    }

    public static final class PoweredFramingSaw extends FramingSawTransferHandler<PoweredFramingSawMenu>
    {
        public PoweredFramingSaw(IRecipeTransferHandlerHelper transferHelper)
        {
            super(transferHelper, PoweredFramingSawMenu.class, FBContent.MENU_TYPE_POWERED_FRAMING_SAW.get());
        }
    }
}
