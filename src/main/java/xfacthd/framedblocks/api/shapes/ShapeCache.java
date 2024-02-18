package xfacthd.framedblocks.api.shapes;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.*;

/**
 * Re-buildable map-like structure mapping keys to {@link VoxelShape} values.<br>
 * Should be used to store preliminary shapes which are used by multiple blocks
 */
public final class ShapeCache<K>
{
    private final Supplier<Map<K, VoxelShape>> mapFactory;
    private final Consumer<Map<K, VoxelShape>> generator;
    private Map<K, VoxelShape> cache;

    /**
     * @deprecated Use factory methods instead
     */
    @Deprecated(forRemoval = true)
    public ShapeCache(Consumer<Map<K, VoxelShape>> generator)
    {
        this(HashMap::new, generator);
    }

    /**
     * @deprecated Use factory methods instead
     */
    @Deprecated(forRemoval = true)
    public ShapeCache(Map<K, VoxelShape> cache, Consumer<Map<K, VoxelShape>> generator)
    {
        this.mapFactory = null;
        this.generator = generator;
        this.cache = cache;
        generator.accept(cache);
        if (!FMLEnvironment.production)
        {
            ShapeReloader.addCache(this);
        }
    }

    private ShapeCache(Supplier<Map<K, VoxelShape>> mapFactory, Consumer<Map<K, VoxelShape>> generator)
    {
        this.mapFactory = mapFactory;
        this.generator = generator;
        this.cache = mapFactory.get();
        generator.accept(cache);
        if (!FMLEnvironment.production)
        {
            ShapeReloader.addCache(this);
        }
    }

    public VoxelShape get(K key)
    {
        return cache.get(key);
    }

    public void forEach(BiConsumer<K, VoxelShape> consumer)
    {
        cache.forEach(consumer);
    }

    @ApiStatus.Internal
    public void reload()
    {
        Map<K, VoxelShape> map;
        if (mapFactory != null)
        {
            map = mapFactory.get();
        }
        else
        {
            map = cache;
            cache.clear();
        }
        generator.accept(map);
        cache = map;
    }



    public static <T> ShapeCache<T> create(Consumer<Map<T, VoxelShape>> generator)
    {
        return new ShapeCache<>(HashMap::new, generator);
    }

    public static <T> ShapeCache<T> createIdentity(Consumer<Map<T, VoxelShape>> generator)
    {
        return new ShapeCache<>(IdentityHashMap::new, generator);
    }

    public static <T extends Enum<T>> ShapeCache<T> createEnum(Class<T> enumClazz, Consumer<Map<T, VoxelShape>> generator)
    {
        return new ShapeCache<>(() -> new EnumMap<>(enumClazz), generator);
    }
}
