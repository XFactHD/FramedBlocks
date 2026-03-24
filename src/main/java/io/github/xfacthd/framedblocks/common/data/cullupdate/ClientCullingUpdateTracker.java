package io.github.xfacthd.framedblocks.common.data.cullupdate;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.client.util.ClientTaskQueue;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

public final class ClientCullingUpdateTracker
{
    public static void handleCullingUpdates(long chunkPos, LongSet positions)
    {
        ClientTaskQueue.enqueueClientTask(1, () ->
        {
            Level level = Objects.requireNonNull(Minecraft.getInstance().level);
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
            if (level.hasChunk(ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos)))
            {
                positions.forEach(pos ->
                {
                    blockPos.set(pos);
                    if (level.getBlockEntity(blockPos) instanceof IFramedBlockEntity be)
                    {
                        be.updateCulling(true, true);
                    }
                });
            }
        });
    }

    private ClientCullingUpdateTracker() { }
}
