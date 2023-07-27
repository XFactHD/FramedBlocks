package xfacthd.framedblocks.common.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.framedblocks.client.util.ClientAccess;

import java.util.function.Supplier;

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

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            if (FMLEnvironment.dist.isClient())
            {
                ClientAccess.openSignScreen(pos, frontText);
            }
        });
        return true;
    }
}