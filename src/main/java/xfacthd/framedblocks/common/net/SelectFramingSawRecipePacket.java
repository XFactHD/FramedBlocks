package xfacthd.framedblocks.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.Objects;
import java.util.function.Supplier;

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

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ServerPlayer player = Objects.requireNonNull(ctx.get().getSender());
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId == containerId && menu instanceof FramingSawMenu)
        {
            menu.clickMenuButton(player, recipeIdx);
        }
    }
}
