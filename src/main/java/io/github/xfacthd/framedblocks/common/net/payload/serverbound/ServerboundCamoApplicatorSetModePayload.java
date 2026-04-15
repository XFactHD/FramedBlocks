package io.github.xfacthd.framedblocks.common.net.payload.serverbound;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorConfig;
import io.github.xfacthd.framedblocks.common.menu.CamoApplicatorMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundCamoApplicatorSetModePayload(int containerId, CamoApplicatorConfig.Mode mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundCamoApplicatorSetModePayload> TYPE = Utils.payloadType("camo_applicator_set_mode");
    public static final StreamCodec<ByteBuf, ServerboundCamoApplicatorSetModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundCamoApplicatorSetModePayload::containerId,
            CamoApplicatorConfig.Mode.STREAM_CODEC,
            ServerboundCamoApplicatorSetModePayload::mode,
            ServerboundCamoApplicatorSetModePayload::new
    );

    public void handle(IPayloadContext ctx) {
        Player player = ctx.player();
        if (player.containerMenu instanceof CamoApplicatorMenu menu && menu.containerId == containerId) {
            menu.setMode(mode);
        }
    }

    @Override
    public Type<ServerboundCamoApplicatorSetModePayload> type() {
        return TYPE;
    }
}
