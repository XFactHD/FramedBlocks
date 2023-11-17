package xfacthd.framedblocks.common.data.cullupdate;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.NetworkEvent;

public record CullingUpdatePacket(LongSet positions)
{
    public static CullingUpdatePacket decode(FriendlyByteBuf buf)
    {
        int count = buf.readInt();
        LongSet positions = new LongArraySet(count);
        for (int i = 0; i < count; i++)
        {
            positions.add(buf.readLong());
        }
        return new CullingUpdatePacket(positions);
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(positions.size());
        positions.forEach(buf::writeLong);
    }

    public boolean handle(NetworkEvent.Context ctx)
    {
        Long2ObjectMap<CullingUpdateChunk> chunks = new Long2ObjectArrayMap<>();

        positions.forEach(pos ->
        {
            long chunkPos = ChunkPos.asLong(
                    SectionPos.blockToSectionCoord(BlockPos.getX(pos)),
                    SectionPos.blockToSectionCoord(BlockPos.getX(pos))
            );
            CullingUpdateChunk chunk = chunks.computeIfAbsent(chunkPos, cp ->
                    new CullingUpdateChunk(new ChunkPos(cp), new LongArraySet())
            );
            chunk.positions().add(pos);
        });

        ctx.enqueueWork(() -> ClientCullingUpdateTracker.handleCullingUpdates(chunks.values()));

        return true;
    }
}
