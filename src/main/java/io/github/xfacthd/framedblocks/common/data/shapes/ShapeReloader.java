package io.github.xfacthd.framedblocks.common.data.shapes;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.shapes.ReloadableShapeLookup;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public final class ShapeReloader implements ResourceManagerReloadListener {
    public static final ShapeReloader INSTANCE = new ShapeReloader();
    public static final Identifier LISTENER_ID = Utils.id("voxel_shapes");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<ShapeCache<?>> CACHES = new ArrayList<>();
    private static final List<ReloadableShapeLookup> LOOKUPS = new ArrayList<>();

    private ShapeReloader() { }

    public static synchronized void addCache(ShapeCache<?> cache) {
        CACHES.add(cache);
    }

    public static synchronized void addLookup(ReloadableShapeLookup provider) {
        LOOKUPS.add(provider);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        reload();
    }

    public static boolean reload() {
        LOGGER.info("Reloading {} caches and {} reloadable shape lookups...", CACHES.size(), LOOKUPS.size());
        Stopwatch watch = Stopwatch.createStarted();
        try {
            CACHES.forEach(ShapeCache::reload);
            LOOKUPS.forEach(ReloadableShapeLookup::reload);
        } catch (Throwable t) {
            LogUtils.getLogger().error("Encountered an error while reloading shapes", t);
            return false;
        }
        watch.stop();
        LOGGER.info("{} caches and {} reloadable shape lookups reloaded, took {}", CACHES.size(), LOOKUPS.size(), watch);

        watch = Stopwatch.createStarted();
        List<BlockState> states = LOOKUPS.stream()
                .map(ReloadableShapeLookup::getStates)
                .flatMap(List::stream)
                .distinct()
                .toList();
        // Rebuild vanilla state cache to update cached occlusion shapes
        states.forEach(BlockBehaviour.BlockStateBase::initCache);
        watch.stop();
        LOGGER.info("Rebuilt vanilla state caches for {} states, took {}", states.size(), watch);
        return true;
    }
}
