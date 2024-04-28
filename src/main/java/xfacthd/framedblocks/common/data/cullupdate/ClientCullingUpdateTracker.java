package xfacthd.framedblocks.common.data.cullupdate;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.client.util.ClientTaskQueue;
import xfacthd.framedblocks.common.net.payload.CullingUpdatePayload;

import java.util.Collection;
import java.util.Objects;

public final class ClientCullingUpdateTracker
{
    public static void handleCullingUpdates(CullingUpdatePayload payload, IPayloadContext ctx)
    {
        Long2ObjectMap<CullingUpdateChunk> chunks = new Long2ObjectArrayMap<>();

        payload.positions().forEach(pos ->
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

        ctx.enqueueWork(() -> handleCullingUpdates(chunks.values()));
    }

    private static void handleCullingUpdates(Collection<CullingUpdateChunk> chunks)
    {
        ClientTaskQueue.enqueueClientTask(1, () ->
        {
            Level level = Objects.requireNonNull(Minecraft.getInstance().level);
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
            chunks.forEach(chunk ->
            {
                ChunkPos cp = chunk.chunk();
                if (level.hasChunk(cp.x, cp.z))
                {
                    chunk.positions().forEach(pos ->
                    {
                        blockPos.set(pos);
                        if (level.getBlockEntity(blockPos) instanceof FramedBlockEntity be)
                        {
                            be.updateCulling(true, true);
                        }
                    });
                }
            });
        });
    }



    private ClientCullingUpdateTracker() { }
}
