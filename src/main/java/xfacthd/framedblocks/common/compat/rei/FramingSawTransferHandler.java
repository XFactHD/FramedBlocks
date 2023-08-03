package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import net.minecraft.client.Minecraft;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

public final class FramingSawTransferHandler implements TransferHandler
{
    @Override
    public Result handle(Context ctx)
    {
        if (!(ctx.getMenu() instanceof FramingSawMenu menu) || !(ctx.getDisplay() instanceof FramingSawDisplay display))
        {
            return Result.createNotApplicable();
        }

        int idx = FramingSawRecipeCache.get(true).getRecipes().indexOf(display.getRecipe());
        if (idx > -1 && menu.isValidRecipeIndex(idx))
        {
            // TODO: implement actual transfer (can't defer to basic transfer handler)

            Minecraft minecraft = ctx.getMinecraft();
            //noinspection ConstantConditions
            if (ctx.isActuallyCrafting() && menu.clickMenuButton(minecraft.player, idx))
            {
                //noinspection ConstantConditions
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, idx);
                minecraft.setScreen(ctx.getContainerScreen());
            }
            return Result.createSuccessful().tooltip(JeiCompat.MSG_TRANSFER_NOT_IMPLEMENTED).color(0x80FFA500);
        }
        return Result.createFailed(JeiCompat.MSG_INVALID_RECIPE);
    }
}
