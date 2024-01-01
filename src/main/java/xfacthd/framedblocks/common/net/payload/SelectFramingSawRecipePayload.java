package xfacthd.framedblocks.common.net.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.menu.IFramingSawMenu;

public record SelectFramingSawRecipePayload(int containerId, int recipeIdx) implements CustomPacketPayload
{
    public static final ResourceLocation ID = Utils.rl("select_framing_saw_recipe");

    public SelectFramingSawRecipePayload(FriendlyByteBuf buf)
    {
        this(buf.readInt(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf)
    {
        buf.writeInt(containerId);
        buf.writeInt(recipeIdx);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public void handle(PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            Player player = ctx.player().orElseThrow();
            AbstractContainerMenu menu = player.containerMenu;
            if (menu.containerId == containerId && menu instanceof IFramingSawMenu)
            {
                menu.clickMenuButton(player, recipeIdx);
            }
        });
    }
}
