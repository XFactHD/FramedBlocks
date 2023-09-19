package xfacthd.framedblocks.api.model.data;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface QuadMap extends Map<Direction, List<BakedQuad>>
{
    ArrayList<BakedQuad> get(Direction side);

    ArrayList<BakedQuad> put(Direction side, ArrayList<BakedQuad> quadList);



    @Override
    default int size()
    {
        return QuadTable.SIDE_COUNT;
    }

    @Override
    default boolean isEmpty()
    {
        return false;
    }

    @Override
    default boolean containsKey(Object key)
    {
        return key instanceof Direction;
    }

    @Override
    default boolean containsValue(Object value)
    {
        return false;
    }

    @Override
    default List<BakedQuad> get(Object key)
    {
        return get((Direction) key);
    }

    @Nullable
    @Override
    default List<BakedQuad> put(Direction key, List<BakedQuad> value)
    {
        if (!(value instanceof ArrayList<BakedQuad>))
        {
            value = new ArrayList<>(value);
        }
        return put(key, (ArrayList<BakedQuad>) value);
    }

    @Override
    default List<BakedQuad> remove(Object key)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void putAll(Map<? extends Direction, ? extends List<BakedQuad>> m)
    {
        m.forEach(this::put);
    }

    @Override
    default void clear()
    {
        throw new UnsupportedOperationException();
    }
}
