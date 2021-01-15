package xfacthd.framedblocks.common.net;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.framedblocks.client.FBClient;

import java.util.function.Supplier;

public class OpenSignScreenPacket
{
    private final BlockPos pos;

    public OpenSignScreenPacket(BlockPos pos) { this.pos = pos; }

    public OpenSignScreenPacket(PacketBuffer buffer) { this.pos = buffer.readBlockPos(); }

    public void encode(PacketBuffer buffer) { buffer.writeBlockPos(pos); }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FBClient.openSignScreen(pos)));
        return true;
    }
}