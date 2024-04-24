package xfacthd.framedblocks.common.data.cullupdate;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xfacthd.framedblocks.common.net.payload.CullingUpdatePayload;

import java.util.IdentityHashMap;
import java.util.Map;

public final class CullingUpdateTracker
{
    private static final Map<ResourceKey<Level>, LongSet> UPDATED_POSITIONS = new IdentityHashMap<>();

    public static void onServerLevelTick(final TickEvent.LevelTickEvent event)
    {
        // Send updates at the start of the next tick to ensure receipt after block update packet
        if (event.phase != TickEvent.Phase.START || event.level.isClientSide())
        {
            return;
        }

        ResourceKey<Level> dim = event.level.dimension();
        LongSet positions = UPDATED_POSITIONS.get(dim);
        if (positions != null && !positions.isEmpty())
        {
            PacketDistributor.sendToPlayersInDimension((ServerLevel)event.level, new CullingUpdatePayload(new LongArraySet(positions)));
            positions.clear();
        }
    }

    public static void enqueueCullingUpdate(Level level, BlockPos pos)
    {
        UPDATED_POSITIONS.computeIfAbsent(level.dimension(), $ -> new LongArraySet()).add(pos.asLong());
    }



    private CullingUpdateTracker() { }
}
