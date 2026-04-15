package io.github.xfacthd.framedblocks.common.net.payload.serverbound;

import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.menu.CamoApplicatorMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundCamoApplicatorConfigureModifierPayload(int containerId, FrameModifier modifier, boolean active) implements CustomPacketPayload {
    public static final Type<ServerboundCamoApplicatorConfigureModifierPayload> TYPE = Utils.payloadType("camo_applicator_configure_modifier");
    public static final StreamCodec<ByteBuf, ServerboundCamoApplicatorConfigureModifierPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundCamoApplicatorConfigureModifierPayload::containerId,
            FrameModifier.STREAM_CODEC,
            ServerboundCamoApplicatorConfigureModifierPayload::modifier,
            ByteBufCodecs.BOOL,
            ServerboundCamoApplicatorConfigureModifierPayload::active,
            ServerboundCamoApplicatorConfigureModifierPayload::new
    );

    public void handle(IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof CamoApplicatorMenu menu && menu.containerId == containerId) {
            menu.configureModifier(modifier, active);
        }
    }

    @Override
    public Type<ServerboundCamoApplicatorConfigureModifierPayload> type() {
        return TYPE;
    }
}
