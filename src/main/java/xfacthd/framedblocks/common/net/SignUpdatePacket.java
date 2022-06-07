package xfacthd.framedblocks.common.net;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;

import java.util.function.Supplier;

public class SignUpdatePacket
{
    private final BlockPos pos;
    private final String[] lines;

    public SignUpdatePacket(BlockPos pos, String[] lines)
    {
        this.pos = pos;
        this.lines = lines;
    }

    public SignUpdatePacket(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();

        int count = buffer.readByte();
        lines = new String[count];
        for (int i = 0; i < count; i++)
        {
            lines[i] = buffer.readUtf(384);
        }
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);

        buffer.writeByte(lines.length);
        for (String line : lines)
        {
            buffer.writeUtf(line);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            //noinspection ConstantConditions
            Level level = player.getLevel();

            //noinspection deprecation
            if (level.hasChunkAt(pos))
            {
                if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
                {
                    if (sign.getEditingPlayer() != player)
                    {
                        FramedBlocks.LOGGER.warn("Player " + player + " tried to edit sign at " + pos);
                        return;
                    }

                    for (int i = 0; i < lines.length; i++)
                    {
                        String line = ChatFormatting.stripFormatting(lines[i]);
                        sign.setLine(i, Component.literal(line != null ? line : ""));
                    }
                }
            }
        });
        return true;
    }
}