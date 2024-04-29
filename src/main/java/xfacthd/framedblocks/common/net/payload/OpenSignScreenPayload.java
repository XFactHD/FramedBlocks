package xfacthd.framedblocks.common.net.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientAccess;

public record OpenSignScreenPayload(BlockPos pos, boolean frontText) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<OpenSignScreenPayload> TYPE = Utils.payloadType("open_sign_screen");
    public static final StreamCodec<FriendlyByteBuf, OpenSignScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OpenSignScreenPayload::pos,
            ByteBufCodecs.BOOL,
            OpenSignScreenPayload::frontText,
            OpenSignScreenPayload::new
    );

    @Override
    public CustomPacketPayload.Type<OpenSignScreenPayload> type()
    {
        return TYPE;
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