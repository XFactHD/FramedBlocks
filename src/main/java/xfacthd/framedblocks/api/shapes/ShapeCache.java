package xfacthd.framedblocks.api.shapes;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.internal.InternalAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Re-buildable map-like structure mapping keys to {@link VoxelShape} values.<br>
 * Should be used to store preliminary shapes which are used by multiple blocks
 */
public final class ShapeCache<K>
{
    private final Map<K, VoxelShape> cache;
    private final Consumer<Map<K, VoxelShape>> generator;

    public ShapeCache(Consumer<Map<K, VoxelShape>> generator)
    {
        this(new HashMap<>(), generator);
    }

    public ShapeCache(Map<K, VoxelShape> cache, Consumer<Map<K, VoxelShape>> generator)
    {
        this.cache = cache;
        this.generator = generator;
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

    @ApiStatus.Internal
    public void reload()
    {
        cache.clear();
        generator.accept(cache);
    }
}
