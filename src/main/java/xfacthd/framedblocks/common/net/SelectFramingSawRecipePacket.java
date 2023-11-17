package xfacthd.framedblocks.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.NetworkEvent;
import xfacthd.framedblocks.common.menu.IFramingSawMenu;

import java.util.Objects;

public record SelectFramingSawRecipePacket(int containerId, int recipeIdx)
{
    public SelectFramingSawRecipePacket(FriendlyByteBuf buf)
    {
        this(buf.readInt(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(containerId);
        buf.writeInt(recipeIdx);
    }

    public void handle(NetworkEvent.Context ctx)
    {
        ServerPlayer player = Objects.requireNonNull(ctx.getSender());
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId == containerId && menu instanceof IFramingSawMenu)
        {
            menu.clickMenuButton(player, recipeIdx);
        }
    }
}
