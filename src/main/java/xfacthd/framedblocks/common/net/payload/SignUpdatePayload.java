package xfacthd.framedblocks.common.net.payload;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.List;
import java.util.stream.Stream;

public record SignUpdatePayload(BlockPos pos, boolean front, String[] lines) implements CustomPacketPayload
{
    public static final ResourceLocation ID = Utils.rl("sign_update");

    public static SignUpdatePayload decode(FriendlyByteBuf buffer)
    {
        BlockPos pos = buffer.readBlockPos();
        boolean front = buffer.readBoolean();

        int count = buffer.readByte();
        String[] lines = new String[count];
        for (int i = 0; i < count; i++)
        {
            lines[i] = buffer.readUtf(384);
        }

        return new SignUpdatePayload(pos, front, lines);
    }

    @Override
    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(front);

        buffer.writeByte(lines.length);
        for (String line : lines)
        {
            buffer.writeUtf(line);
        }
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public void handle(PlayPayloadContext ctx)
    {
        ServerPlayer player = (ServerPlayer) ctx.player().orElseThrow();
        List<String> strippedLines = Stream.of(lines).map(ChatFormatting::stripFormatting).toList();
        player.connection.filterTextPacket(strippedLines).thenAccept(filteredText -> ctx.workHandler().submitAsync(() ->
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