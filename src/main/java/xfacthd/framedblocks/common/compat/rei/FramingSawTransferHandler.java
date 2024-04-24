package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.PacketDistributor;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.IFramingSawMenu;
import xfacthd.framedblocks.common.net.payload.SelectFramingSawRecipePayload;

public final class FramingSawTransferHandler implements TransferHandler
{
    @Override
    public Result handle(Context ctx)
    {
        AbstractContainerMenu menu = ctx.getMenu();
        if (!(menu instanceof IFramingSawMenu sawMenu) || !(ctx.getDisplay() instanceof FramingSawDisplay display))
        {
            return Result.createNotApplicable();
        }

        int idx = FramingSawRecipeCache.get(true).getRecipes().indexOf(display.getRecipe());
        if (idx > -1 && sawMenu.isValidRecipeIndex(idx))
        {
            // TODO: implement actual transfer (can't defer to basic transfer handler)

            Minecraft minecraft = ctx.getMinecraft();
            //noinspection ConstantConditions
            if (ctx.isActuallyCrafting() && menu.clickMenuButton(minecraft.player, idx))
            {
                PacketDistributor.sendToServer(new SelectFramingSawRecipePayload(menu.containerId, idx));
                minecraft.setScreen(ctx.getContainerScreen());
            }
            return Result.createSuccessful().tooltip(JeiCompat.MSG_TRANSFER_NOT_IMPLEMENTED).color(0x80FFA500);
        }
        return Result.createFailed(JeiCompat.MSG_INVALID_RECIPE);
    }
}
