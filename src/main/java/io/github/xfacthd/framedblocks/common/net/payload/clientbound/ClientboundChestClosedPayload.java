package io.github.xfacthd.framedblocks.common.net.payload.clientbound;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundChestClosedPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ClientboundChestClosedPayload> TYPE = Utils.payloadType("chest_closed");
    public static final StreamCodec<ByteBuf, ClientboundChestClosedPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundChestClosedPayload::pos,
            ClientboundChestClosedPayload::new
    );

    @Override
    public Type<ClientboundChestClosedPayload> type() {
        return TYPE;
    }
}
