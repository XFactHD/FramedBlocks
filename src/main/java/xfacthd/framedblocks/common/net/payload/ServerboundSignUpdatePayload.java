package xfacthd.framedblocks.common.net.payload;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedByteBufCodecs;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.List;
import java.util.stream.Stream;

public record ServerboundSignUpdatePayload(BlockPos pos, boolean front, String[] lines) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ServerboundSignUpdatePayload> TYPE = Utils.payloadType("sign_update");
    public static final StreamCodec<FriendlyByteBuf, ServerboundSignUpdatePayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ServerboundSignUpdatePayload::pos,
            ByteBufCodecs.BOOL,
            ServerboundSignUpdatePayload::front,
            FramedByteBufCodecs.array(ByteBufCodecs.stringUtf8(384), String[]::new, 4),
            ServerboundSignUpdatePayload::lines,
            ServerboundSignUpdatePayload::new
    );

    @Override
    public CustomPacketPayload.Type<ServerboundSignUpdatePayload> type()
    {
        return TYPE;
    }

    public void handle(IPayloadContext ctx)
    {
        ServerPlayer player = (ServerPlayer) ctx.player();
        List<String> strippedLines = Stream.of(lines).map(ChatFormatting::stripFormatting).toList();
        player.connection.filterTextPacket(strippedLines).thenAccept(filteredText -> ctx.enqueueWork(() ->
        {
            Level level = player.level();

            //noinspection deprecation
            if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
            {
                if (sign.isWaxed() || !player.getUUID().equals(sign.getEditingPlayer()))
                {
                    FramedBlocks.LOGGER.warn(
                            "Player {} just tried to change non-editable sign at {}", player.getName().getString(), pos
                    );
                    return;
                }

                player.resetLastActionTime();
                sign.updateTextFromPacket(player, front, filteredText);
            }
        }));
    }
}
