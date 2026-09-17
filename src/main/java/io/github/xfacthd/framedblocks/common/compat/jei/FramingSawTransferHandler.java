package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.IFramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.PoweredFramingSawMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundSelectFramingSawRecipePayload;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public abstract sealed class FramingSawTransferHandler<C extends AbstractContainerMenu & IFramingSawMenu> implements IRecipeTransferHandler<C, FramingSawRecipe> {
    private final IRecipeTransferHandlerHelper transferHelper;
    private final IRecipeTransferInfo<C, FramingSawRecipe> transferInfo;
    private final IRecipeTransferHandler<C, FramingSawRecipe> wrappedHandler;

    private FramingSawTransferHandler(IRecipeTransferHandlerHelper transferHelper, Class<? extends C> menuClass, MenuType<C> menuType) {
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
    public Class<? extends C> getContainerClass() {
        return transferInfo.getContainerClass();
    }

    @Override
    public Optional<MenuType<C>> getMenuType() {
        return transferInfo.getMenuType();
    }

    @Override
    public IRecipeType<FramingSawRecipe> getRecipeType() {
        return transferInfo.getRecipeType();
    }

    @Override
    @SuppressWarnings("removal")
    public @Nullable IRecipeTransferError transferRecipe(C menu, FramingSawRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(IRecipeTransferContext<FramingSawRecipe, C> context, boolean doTransfer) {
        int idx = FramingSawRecipeCache.get(true).getRecipeIndex(context.getRecipe());
        C menu = context.getContainer();
        if (idx != -1 && menu.isValidRecipeIndex(idx)) {
            IRecipeTransferError error = wrappedHandler.transferRecipe(context, doTransfer);
            if (error != null) {
                return error;
            }

            if (doTransfer && menu.clickMenuButton(context.getPlayer(), idx)) {
                ClientPacketDistributor.sendToServer(new ServerboundSelectFramingSawRecipePayload(menu.containerId, idx));
            }
            return null;
        }
        return transferHelper.createUserErrorWithTooltip(JeiCompat.MSG_INVALID_RECIPE);
    }

    public static final class FramingSaw extends FramingSawTransferHandler<FramingSawMenu> {
        public FramingSaw(IRecipeTransferHandlerHelper transferHelper) {
            super(transferHelper, FramingSawMenu.class, FBContent.MENU_TYPE_FRAMING_SAW.get());
        }
    }

    public static final class PoweredFramingSaw extends FramingSawTransferHandler<PoweredFramingSawMenu> {
        public PoweredFramingSaw(IRecipeTransferHandlerHelper transferHelper) {
            super(transferHelper, PoweredFramingSawMenu.class, FBContent.MENU_TYPE_POWERED_FRAMING_SAW.get());
        }
    }
}
