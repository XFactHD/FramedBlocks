package xfacthd.framedblocks.common.net.payload;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xfacthd.framedblocks.api.util.FramedByteBufCodecs;
import xfacthd.framedblocks.api.util.Utils;

public record ClientboundCullingUpdatePayload(long chunk, LongSet positions) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundCullingUpdatePayload> TYPE = Utils.payloadType("culling_update");
    public static final StreamCodec<FriendlyByteBuf, ClientboundCullingUpdatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            ClientboundCullingUpdatePayload::chunk,
            FramedByteBufCodecs.longCollection(LongArraySet::new),
            ClientboundCullingUpdatePayload::positions,
            ClientboundCullingUpdatePayload::new
    );

    @Override
    public CustomPacketPayload.Type<ClientboundCullingUpdatePayload> type()
    {
        return TYPE;
    }
}
