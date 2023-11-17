package xfacthd.framedblocks.common.data;

import com.google.common.base.Stopwatch;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.registries.ForgeRegistries;
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
        Map<Block, List<StateCache>> dedupMap = new HashMap<>();
        long count = ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block instanceof IFramedBlock)
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream)
                .peek(state ->
                {
                    StateCache cache = ((IFramedBlock) state.getBlock()).initCache(state);
                    if (cache.equals(StateCache.EMPTY))
                    {
                        ((IStateCacheAccessor) state).framedblocks$initCache(StateCache.EMPTY);
                        return;
                    }

                    List<StateCache> caches = dedupMap.computeIfAbsent(state.getBlock(), $ -> new ArrayList<>());
                    StateCache deduped = caches.stream().filter(cache::equals).findFirst().orElseGet(() ->
                    {
                        caches.add(cache);
                        return cache;
                    });
                    ((IStateCacheAccessor) state).framedblocks$initCache(deduped);
                })
                .count();
        watch.stop();
        long unique = dedupMap.values().stream().mapToLong(List::size).sum() + 1; // +1 for the empty instance
        FramedBlocks.LOGGER.debug("Initialized {} unique caches for {} states in {}", unique, count, watch);
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
