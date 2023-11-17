package xfacthd.framedblocks.common.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;
import xfacthd.framedblocks.client.util.ClientAccess;

public record OpenSignScreenPacket(BlockPos pos, boolean frontText)
{
    public OpenSignScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(frontText);
    }

    public boolean handle(NetworkEvent.Context ctx)
    {
        ctx.enqueueWork(() ->
        {
            if (FMLEnvironment.dist.isClient())
            {
                ClientAccess.openSignScreen(pos, frontText);
            }
        });
        return true;
    }
}