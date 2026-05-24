package xfacthd.framedblocks.common.data;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.IStateCacheAccessor;
import xfacthd.framedblocks.api.block.cache.StateCache;

import java.util.*;

public final class StateCacheBuilder
{
    private static volatile boolean cachesBuilt = false;

    public static void ensureStateCachesInitialized()
    {
        if (!cachesBuilt)
        {
            synchronized (StateCacheBuilder.class)
            {
                if (!cachesBuilt)
                {
                    initializeStateCaches();
                    cachesBuilt = true;
                }
            }
        }
    }

    private static void initializeStateCaches()
    {
        FramedBlocks.LOGGER.debug("Initializing custom state metadata caches");
        Stopwatch watch = Stopwatch.createStarted();
        long[] stateCount = new long[] { 0 };
        ObjectOpenHashSet<StateCache> cacheDedup = new ObjectOpenHashSet<>();
        cacheDedup.add(StateCache.EMPTY);
        BuiltInRegistries.BLOCK.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(block -> block instanceof IFramedBlock)
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .forEach(states ->
                {
                    for (BlockState state : states)
                    {
                        StateCache cache = ((IFramedBlock) state.getBlock()).initCache(state);
                        cache = cacheDedup.addOrGet(cache);
                        ((IStateCacheAccessor) state).framedblocks$initCache(cache);
                    }
                    stateCount[0] += states.size();
                });
        watch.stop();
        FramedBlocks.LOGGER.debug("Initialized {} unique caches for {} states in {}", cacheDedup.size(), stateCount[0], watch);
    }



    public static final class CacheReloader implements ResourceManagerReloadListener
    {
        public static final CacheReloader INSTANCE = new CacheReloader();

        private CacheReloader() { }

        @Override
        public void onResourceManagerReload(ResourceManager mgr)
        {
            initializeStateCaches();
        }
    }



    private StateCacheBuilder() { }
}
