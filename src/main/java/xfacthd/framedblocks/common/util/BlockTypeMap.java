package xfacthd.framedblocks.common.util;

import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class BlockTypeMap<T>
{
    private static final BlockType[] TYPES = BlockType.values();
    private static final int TYPE_COUNT = TYPES.length;

    private final T defaultValue;
    private final Object[] values = new Object[TYPE_COUNT];

    protected BlockTypeMap(T defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public final void initialize()
    {
        fill();
        if (!FMLEnvironment.production)
        {
            check();
        }
    }

    protected abstract void fill();

    private void check()
    {
        int missing = 0;
        for (int i = 0; i < TYPE_COUNT; i++)
        {
            if (values[i] == null)
            {
                missing++;
                FramedBlocks.LOGGER.error(
                        "Type '{}' missing mapping in '{}'", TYPES[i], getClass().getSimpleName()
                );
            }
        }
        if (missing > 0)
        {
            FramedBlocks.LOGGER.error("Found {} missing mappings in '{}'", missing, getClass().getSimpleName());
        }
    }

    protected final void put(BlockType type, T value)
    {
        values[type.ordinal()] = Objects.requireNonNull(value);
    }

    @SuppressWarnings("unchecked")
    public final T get(BlockType type)
    {
        Object value = values[Objects.requireNonNull(type).ordinal()];
        if (value != null)
        {
            return (T) value;
        }
        return defaultValue;
    }

    public final void forEach(BiConsumer<BlockType, T> consumer)
    {
        for (BlockType type : TYPES)
        {
            consumer.accept(type, get(type));
        }
    }
}
