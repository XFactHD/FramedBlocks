package xfacthd.framedblocks.common.data.cullupdate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.client.util.ClientTaskQueue;
import xfacthd.framedblocks.common.net.payload.ClientboundCullingUpdatePayload;

import java.util.Objects;

public final class ClientCullingUpdateTracker
{
    public static void handleCullingUpdates(ClientboundCullingUpdatePayload payload, IPayloadContext ctx)
    {
        CullingUpdateChunk chunk = new CullingUpdateChunk(new ChunkPos(payload.chunk()), payload.positions());
        ctx.enqueueWork(() -> handleCullingUpdates(chunk));
    }

    private static void handleCullingUpdates(CullingUpdateChunk chunk)
    {
        ClientTaskQueue.enqueueClientTask(1, () ->
        {
            Level level = Objects.requireNonNull(Minecraft.getInstance().level);
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
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
    }



    private ClientCullingUpdateTracker() { }
}
