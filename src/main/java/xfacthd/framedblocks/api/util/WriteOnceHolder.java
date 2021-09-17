package xfacthd.framedblocks.api.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WriteOnceHolder<T> implements Supplier<T>, Consumer<T>
{
    private T value = null;

    @Override
    public T get()
    {
        if (value == null)
        {
            throw new IllegalStateException("Tried to retrieve value before initialization!");
        }
        return value;
    }

    @Override
    public void accept(T t)
    {
        if (value != null)
        {
            throw new IllegalStateException("Tried to set value again after initialization!");
        }
        value = t;
    }
}