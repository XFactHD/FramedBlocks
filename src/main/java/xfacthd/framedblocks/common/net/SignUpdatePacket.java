package xfacthd.framedblocks.common.net;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record SignUpdatePacket(BlockPos pos, boolean front, String[] lines)
{
    public static SignUpdatePacket decode(FriendlyByteBuf buffer)
    {
        BlockPos pos = buffer.readBlockPos();
        boolean front = buffer.readBoolean();

        int count = buffer.readByte();
        String[] lines = new String[count];
        for (int i = 0; i < count; i++)
        {
            lines[i] = buffer.readUtf(384);
        }

        return new SignUpdatePacket(pos, front, lines);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(front);

        buffer.writeByte(lines.length);
        for (String line : lines)
        {
            buffer.writeUtf(line);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ServerPlayer player = Objects.requireNonNull(ctx.get().getSender());
        List<String> strippedLines = Stream.of(lines).map(ChatFormatting::stripFormatting).toList();
        player.connection.filterTextPacket(strippedLines).thenAccept(filteredText -> ctx.get().enqueueWork(() ->
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
        return true;
    }
}