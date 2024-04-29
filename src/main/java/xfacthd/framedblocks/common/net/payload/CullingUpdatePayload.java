package xfacthd.framedblocks.common.net.payload;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.net.FramedByteBufCodecs;

public record CullingUpdatePayload(LongSet positions) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<CullingUpdatePayload> TYPE = Utils.payloadType("culling_update");
    public static final StreamCodec<FriendlyByteBuf, CullingUpdatePayload> CODEC = StreamCodec.composite(
            FramedByteBufCodecs.longCollection(LongArraySet::new),
            CullingUpdatePayload::positions,
            CullingUpdatePayload::new
    );

    @Override
    public CustomPacketPayload.Type<CullingUpdatePayload> type()
    {
        return TYPE;
    }
}
