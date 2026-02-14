package io.github.xfacthd.framedblocks.common.util;

import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class BlockTypeMap<T>
{
    private static final BlockType[] TYPES = BlockType.values();
    private static final int TYPE_COUNT = TYPES.length;

    private final T defaultValue;
    @Nullable
    private final Predicate<BlockType> setDefault;
    private final @Nullable Object[] values = new Object[TYPE_COUNT];

    protected BlockTypeMap(T defaultValue)
    {
        this(defaultValue, null);
    }

    protected BlockTypeMap(T defaultValue, @Nullable Predicate<BlockType> setDefault)
    {
        this.defaultValue = defaultValue;
        this.setDefault = setDefault;
    }

    public final void initialize()
    {
        if (setDefault != null)
        {
            for (BlockType type : TYPES)
            {
                if (setDefault.test(type))
                {
                    put(type, defaultValue);
                }
            }
        }
        fill();
        if (!Utils.PRODUCTION)
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
        if (!Utils.PRODUCTION && values[type.ordinal()] != null)
        {
            throw new IllegalStateException(String.format(
                    "Tried to overwrite mapping assigned to '%s' in '%s' (old: %s, new: %s)",
                    type,
                    getClass().getSimpleName(),
                    values[type.ordinal()],
                    value
            ));
        }
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
