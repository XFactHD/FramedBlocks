package xfacthd.framedblocks.common.net.payload;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import xfacthd.framedblocks.api.util.Utils;

public record CullingUpdatePayload(LongSet positions) implements CustomPacketPayload
{
    public static final ResourceLocation ID = Utils.rl("culling_update");

    public static CullingUpdatePayload decode(FriendlyByteBuf buf)
    {
        int count = buf.readInt();
        LongSet positions = new LongArraySet(count);
        for (int i = 0; i < count; i++)
        {
            positions.add(buf.readLong());
        }
        return new CullingUpdatePayload(positions);
    }

    @Override
    public void write(FriendlyByteBuf buf)
    {
        buf.writeInt(positions.size());
        positions.forEach(buf::writeLong);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }
}
