package xfacthd.framedblocks.common.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.framedblocks.client.util.ClientAccess;

import java.util.function.Supplier;

public class OpenSignScreenPacket
{
    private final BlockPos pos;

    public OpenSignScreenPacket(BlockPos pos) { this.pos = pos; }

    public OpenSignScreenPacket(FriendlyByteBuf buffer) { this.pos = buffer.readBlockPos(); }

    public void encode(FriendlyByteBuf buffer) { buffer.writeBlockPos(pos); }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            if (FMLEnvironment.dist.isClient())
            {
                ClientAccess.openSignScreen(pos);
            }
        });
        return true;
    }
}