package xfacthd.framedblocks.api.shapes;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.internal.InternalAPI;

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

    private ShapeCache(Supplier<Map<K, VoxelShape>> mapFactory, Consumer<Map<K, VoxelShape>> generator)
    {
        this.mapFactory = mapFactory;
        this.generator = generator;
        this.cache = mapFactory.get();
        generator.accept(cache);
        if (!FMLEnvironment.production)
        {
            InternalAPI.INSTANCE.registerShapeCache(this);
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
        Map<K, VoxelShape> map = mapFactory.get();
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
