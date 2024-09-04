package xfacthd.framedblocks.api.util;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerHelper;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;

import java.util.*;
import java.util.stream.Stream;

public final class CamoList implements Iterable<CamoContainer<?, ?>>
{
    public static final Codec<CamoList> CODEC = CamoContainerHelper.CODEC.listOf().xmap(CamoList::of, list -> List.of(list.camos));
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoList> STREAM_CODEC = FramedByteBufCodecs.<RegistryFriendlyByteBuf, CamoContainer<?, ?>>array(
            CamoContainerHelper.STREAM_CODEC, CamoContainer[]::new, Integer.MAX_VALUE
    ).map(CamoList::new, list -> list.camos);
    public static final CamoList EMPTY = new CamoList(new CamoContainer[0]);

    private final CamoContainer<?, ?>[] camos;
    private final int offset;
    private final int size;

    private CamoList(CamoContainer<?, ?>[] camos)
    {
        this.camos = camos;
        this.offset = 0;
        this.size = camos.length;
    }

    private CamoList(CamoContainer<?, ?>[] camos, int from, int to)
    {
        this.camos = camos;
        this.offset = from;
        this.size = to - from;
    }

    public CamoContainer<?, ?> getCamo(int index)
    {
        if (size > index)
        {
            return camos[offset + index];
        }
        return EmptyCamoContainer.EMPTY;
    }

    public boolean isEmpty()
    {
        return camos.length == 0;
    }

    public boolean isEmptyOrContentsEmpty()
    {
        for (int i = 0; i < size; i++)
        {
            if (!camos[offset + i].isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    public CamoList concat(CamoList other)
    {
        if (isEmpty()) return other;
        if (other.isEmpty()) return this;

        CamoContainer<?, ?>[] array = new CamoContainer[size + other.size];
        System.arraycopy(camos, offset, array, 0, size);
        System.arraycopy(other.camos, other.offset, array, size, other.size);
        return new CamoList(array);
    }

    @Override
    public Iterator<CamoContainer<?, ?>> iterator()
    {
        return ObjectIterators.wrap(camos, offset, size);
    }

    @Override
    public Spliterator<CamoContainer<?, ?>> spliterator()
    {
        return ObjectSpliterators.wrap(camos, offset, size);
    }

    public Stream<CamoContainer<?, ?>> stream()
    {
        return Arrays.stream(camos, offset, offset + size);
    }

    public CamoList reversed()
    {
        return switch (size)
        {
            case 0 -> EMPTY;
            case 1 -> this;
            default -> new CamoList(ObjectArrays.reverse(ObjectArrays.copy(camos, offset, size)));
        };
    }

    public CamoList subList(int fromIndex, int toIndex)
    {
        if (fromIndex < 0 || fromIndex > toIndex)
        {
            throw new IllegalArgumentException("Invalid indizes");
        }
        if (fromIndex >= size)
        {
            return EMPTY;
        }
        return new CamoList(camos, fromIndex, Math.min(toIndex, size));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Arrays.equals(camos, ((CamoList) obj).camos);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(camos);
    }

    @Override
    public String toString()
    {
        return Arrays.toString(camos);
    }



    public static CamoList of(CamoContainer<?, ?> camo)
    {
        return camo.isEmpty() ? EMPTY : new CamoList(new CamoContainer[] { camo });
    }

    public static CamoList of(CamoContainer<?, ?>... camos)
    {
        return camos.length == 0 ? EMPTY : new CamoList(Arrays.copyOf(camos, camos.length));
    }

    public static CamoList of(List<CamoContainer<?, ?>> camos)
    {
        return camos.isEmpty() ? EMPTY : new CamoList(camos.toArray(CamoContainer[]::new));
    }
}
