package xfacthd.framedblocks.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerHelper;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;

import java.util.*;
import java.util.stream.Stream;

public final class CamoList implements Iterable<CamoContainer<?, ?>>
{
    public static final Codec<CamoList> CODEC = CamoContainerHelper.CODEC.listOf().xmap(CamoList::new, list -> list.camos);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoList> STREAM_CODEC = CamoContainerHelper.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(CamoList::new, list -> list.camos);
    public static final CamoList EMPTY = new CamoList(List.of());

    private final List<CamoContainer<?, ?>> camos;

    private CamoList(List<CamoContainer<?, ?>> camos)
    {
        this.camos = camos;
    }

    public CamoContainer<?, ?> getCamo(int index)
    {
        if (camos.size() > index)
        {
            return camos.get(index);
        }
        return EmptyCamoContainer.EMPTY;
    }

    public boolean isEmpty()
    {
        return camos.isEmpty();
    }

    public CamoList concat(CamoList other)
    {
        return new CamoList(Utils.concat(camos, other.camos));
    }

    @Override
    public Iterator<CamoContainer<?, ?>> iterator()
    {
        return camos.listIterator();
    }

    @Override
    public Spliterator<CamoContainer<?, ?>> spliterator()
    {
        return camos.spliterator();
    }

    public Stream<CamoContainer<?, ?>> stream()
    {
        return camos.stream();
    }

    public CamoList reversed()
    {
        return new CamoList(camos.reversed());
    }

    public CamoList subList(int fromIndex, int toIndex)
    {
        if (fromIndex >= camos.size())
        {
            return EMPTY;
        }
        return new CamoList(camos.subList(fromIndex, Math.min(toIndex, camos.size())));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Objects.equals(camos, ((CamoList) obj).camos);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(camos);
    }



    public static CamoList of(CamoContainer<?, ?> camo)
    {
        return new CamoList(List.of(camo));
    }

    public static CamoList of(CamoContainer<?, ?>... camos)
    {
        return new CamoList(List.of(camos));
    }

    public static CamoList of(List<CamoContainer<?, ?>> camos)
    {
        return new CamoList(List.copyOf(camos));
    }
}
