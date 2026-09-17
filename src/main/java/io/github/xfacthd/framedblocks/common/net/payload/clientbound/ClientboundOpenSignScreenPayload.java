package io.github.xfacthd.framedblocks.common.net.payload.clientbound;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.SignTextSlot;

public record ClientboundOpenSignScreenPayload(BlockPos pos, SignTextSlot textSlot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundOpenSignScreenPayload> TYPE = Utils.payloadType("open_sign_screen");
    public static final StreamCodec<FriendlyByteBuf, ClientboundOpenSignScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundOpenSignScreenPayload::pos,
            SignTextSlot.STREAM_CODEC,
            ClientboundOpenSignScreenPayload::textSlot,
            ClientboundOpenSignScreenPayload::new
    );

    @Override
    public CustomPacketPayload.Type<ClientboundOpenSignScreenPayload> type() {
        return TYPE;
    }
}
