package xfacthd.framedblocks.common.data.cullupdate;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xfacthd.framedblocks.common.net.payload.ClientboundCullingUpdatePayload;

import java.util.Map;

public final class CullingUpdateTracker
{
    private static final Map<ResourceKey<Level>, Long2ObjectMap<LongSet>> UPDATED_POSITIONS = new Reference2ObjectOpenHashMap<>();

    // Send updates at the start of the next tick to ensure receipt after block update packet
    public static void onServerLevelTick(final LevelTickEvent.Pre event)
    {
        Level level = event.getLevel();
        if (level.isClientSide())
        {
            return;
        }

        ResourceKey<Level> dim = level.dimension();
        Long2ObjectMap<LongSet> chunks = UPDATED_POSITIONS.get(dim);
        if (chunks != null && !chunks.isEmpty())
        {
            for (Long2ObjectMap.Entry<LongSet> entry : chunks.long2ObjectEntrySet())
            {
                long chunk = entry.getLongKey();
                PacketDistributor.sendToPlayersTrackingChunk(
                        (ServerLevel) level,
                        new ChunkPos(chunk),
                        new ClientboundCullingUpdatePayload(chunk, entry.getValue())
                );
            }
            chunks.clear();
        }
    }

    public static void onServerShutdown(@SuppressWarnings("unused") final ServerStoppedEvent event)
    {
        UPDATED_POSITIONS.clear();
    }

    public static void enqueueCullingUpdate(Level level, BlockPos pos)
    {
        UPDATED_POSITIONS.computeIfAbsent(level.dimension(), $ -> new Long2ObjectOpenHashMap<>())
                .computeIfAbsent(ChunkPos.asLong(pos), $ -> new LongArraySet())
                .add(pos.asLong());
    }



    private CullingUpdateTracker() { }
}
