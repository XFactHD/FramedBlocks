package xfacthd.framedblocks.common.net.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientAccess;

public record OpenSignScreenPayload(BlockPos pos, boolean frontText) implements CustomPacketPayload
{
    public static final ResourceLocation ID = Utils.rl("open_sign_screen");

    public OpenSignScreenPayload(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(frontText);
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
            if (FMLEnvironment.dist.isClient())
            {
                ClientAccess.openSignScreen(pos, frontText);
            }
        });
    }
}