package xfacthd.framedblocks.common.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

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

    public SignUpdatePacket(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();

        int count = buffer.readByte();
        lines = new String[count];
        for (int i = 0; i < count; i++)
        {
            lines[i] = buffer.readString(384);
        }
    }

    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(pos);

        buffer.writeByte(lines.length);
        for (String line : lines)
        {
            buffer.writeString(line);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            //noinspection ConstantConditions
            World world = player.getServerWorld();

            //noinspection deprecation
            if (world.isBlockLoaded(pos))
            {
                if (world.getTileEntity(pos) instanceof FramedSignTileEntity sign)
                {
                    if (sign.getEditingPlayer() != player)
                    {
                        FramedBlocks.LOGGER.warn("Player " + player + " tried to edit sign at " + pos);
                        return;
                    }

                    for (int i = 0; i < lines.length; i++)
                    {
                        String line = TextFormatting.getTextWithoutFormattingCodes(lines[i]);
                        sign.setLine(i, new StringTextComponent(line != null ? line : ""));
                    }
                }
            }
        });
        return true;
    }
}