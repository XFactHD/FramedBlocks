package io.github.xfacthd.framedblocks.common.net.payload.serverbound;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.menu.CamoApplicatorMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundCamoApplicatorSetSlotPayload(int containerId, int selectedSlot) implements CustomPacketPayload {
    public static final Type<ServerboundCamoApplicatorSetSlotPayload> TYPE = Utils.payloadType("camo_applicator_set_slot");
    public static final StreamCodec<ByteBuf, ServerboundCamoApplicatorSetSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundCamoApplicatorSetSlotPayload::containerId,
            ByteBufCodecs.VAR_INT,
            ServerboundCamoApplicatorSetSlotPayload::selectedSlot,
            ServerboundCamoApplicatorSetSlotPayload::new
    );

    public void handle(IPayloadContext ctx) {
        Player player = ctx.player();
        if (player.containerMenu instanceof CamoApplicatorMenu menu && menu.containerId == containerId) {
            menu.setSelectedSlot(selectedSlot);
        }
    }

    @Override
    public Type<ServerboundCamoApplicatorSetSlotPayload> type() {
        return TYPE;
    }
}
