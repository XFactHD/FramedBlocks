package xfacthd.framedblocks.common.net.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientAccess;

public record OpenSignScreenPayload(BlockPos pos, boolean frontText) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<OpenSignScreenPayload> ID = Utils.payloadType("open_sign_screen");

    public OpenSignScreenPayload(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(frontText);
    }

    @Override
    public CustomPacketPayload.Type<OpenSignScreenPayload> type()
    {
        return ID;
    }

    public void handle(IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
            if (FMLEnvironment.dist.isClient())
            {
                ClientAccess.openSignScreen(pos, frontText);
            }
        });
    }
}