package io.github.xfacthd.framedblocks.api.shapes;

import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/// Re-buildable map-like structure mapping keys to [VoxelShape] values.
///
/// Should be used to store preliminary shapes which are used by multiple blocks.
public final class ShapeCache<K> {
    private final Supplier<Map<K, VoxelShape>> mapFactory;
    private final Consumer<Map<K, VoxelShape>> generator;
    private Map<K, VoxelShape> cache;

    private ShapeCache(Supplier<Map<K, VoxelShape>> mapFactory, Consumer<Map<K, VoxelShape>> generator) {
        this.mapFactory = mapFactory;
        this.generator = generator;
        this.cache = mapFactory.get();
        generator.accept(cache);
        if (!Utils.PRODUCTION) {
            InternalAPI.INSTANCE.registerShapeCache(this);
        }
    }

    /// {@return the shape for the given key}
    ///
    /// @param key The key to get the shape for
    public VoxelShape get(K key) {
        return cache.get(key);
    }

    /// Performs the given action for each entry in the backing map.
    ///
    /// @param consumer The action to perform
    public void forEach(BiConsumer<K, VoxelShape> consumer) {
        cache.forEach(consumer);
    }

    @ApiStatus.Internal
    public void reload() {
        Map<K, VoxelShape> map = mapFactory.get();
        generator.accept(map);
        cache = map;
    }

    /// {@return a new cache backed by a hash map and populated by the given generator}
    ///
    /// @param generator The generator to populate the cache with
    public static <T> ShapeCache<T> create(Consumer<Map<T, VoxelShape>> generator) {
        return new ShapeCache<>(HashMap::new, generator);
    }

    /// {@return a new cache backed by an identity hash map and populated by the given generator}
    ///
    /// @param generator The generator to populate the cache with
    public static <T> ShapeCache<T> createIdentity(Consumer<Map<T, VoxelShape>> generator) {
        return new ShapeCache<>(IdentityHashMap::new, generator);
    }

    /// {@return a new cache backed by an enum map for the given enum type and populated by the given generator}
    ///
    /// @param enumClazz The enum type of the map keys
    /// @param generator The generator to populate the cache with
    public static <T extends Enum<T>> ShapeCache<T> createEnum(Class<T> enumClazz, Consumer<Map<T, VoxelShape>> generator) {
        return new ShapeCache<>(() -> new EnumMap<>(enumClazz), generator);
    }
}
