package xfacthd.framedblocks.common.data;

import com.google.common.base.Stopwatch;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.registries.ForgeRegistries;
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
        long[] counts = new long[] { 0, 1 }; // +1 for the empty instance
        ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block instanceof IFramedBlock)
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .forEach(states ->
                {
                    Map<StateCache, StateCache> cacheDedup = new HashMap<>();
                    for (BlockState state : states)
                    {
                        StateCache cache = ((IFramedBlock) state.getBlock()).initCache(state);
                        if (cache.equals(StateCache.EMPTY))
                        {
                            ((IStateCacheAccessor) state).framedblocks$initCache(StateCache.EMPTY);
                        }
                        else
                        {
                            ((IStateCacheAccessor) state).framedblocks$initCache(
                                    Objects.requireNonNullElse(cacheDedup.putIfAbsent(cache, cache), cache)
                            );
                        }
                    }
                    counts[0] += states.size();
                    counts[1] += cacheDedup.size();
                });
        watch.stop();
        FramedBlocks.LOGGER.debug("Initialized {} unique caches for {} states in {}", counts[1], counts[0], watch);
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
