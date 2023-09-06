package xfacthd.framedblocks.common.datagen.builders.book.includes;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class IncludeHolder<T extends IncludeHolder<T>>
{
    protected final Map<String, IncludeBuilder> includes = new LinkedHashMap<>();

    public final T include(IncludeBuilder include)
    {
        IncludeBuilder prev = includes.put(include.getName(), include);
        if (prev != null)
        {
            throw new IllegalStateException("Include with name '" + prev.getName() + "' registered twice");
        }
        //noinspection unchecked
        return (T) this;
    }
}
