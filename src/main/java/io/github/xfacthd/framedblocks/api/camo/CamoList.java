package io.github.xfacthd.framedblocks.api.camo;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.network.FramedByteBufCodecs;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class CamoList implements Iterable<CamoContainer<?, ?>> {
    public static final Codec<CamoList> CODEC = CamoContainerHelper.CODEC.listOf().xmap(CamoList::of, list -> List.of(list.getCamosForSerialization()));
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoList> STREAM_CODEC = FramedByteBufCodecs.<RegistryFriendlyByteBuf, CamoContainer<?, ?>>array(
            CamoContainerHelper.STREAM_CODEC, CamoContainer[]::new, Integer.MAX_VALUE
    ).map(CamoList::new, CamoList::getCamosForSerialization);
    public static final CamoList EMPTY = new CamoList(new CamoContainer[0]);

    private final CamoContainer<?, ?>[] camos;
    private final int offset;
    private final int size;

    private CamoList(CamoContainer<?, ?>[] camos) {
        this.camos = camos;
        this.offset = 0;
        this.size = camos.length;
    }

    private CamoList(CamoContainer<?, ?>[] camos, int from, int to) {
        this.camos = camos;
        this.offset = from;
        this.size = to - from;
    }

    public CamoContainer<?, ?> getCamo(int index) {
        if (size > index) {
            return camos[offset + index];
        }
        return EmptyCamoContainer.EMPTY;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isEmptyOrContentsEmpty() {
        int end = offset + size;
        for (int i = offset; i < end; i++) {
            if (!camos[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public CamoList concat(CamoList other) {
        if (isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }

        CamoContainer<?, ?>[] array = new CamoContainer[size + other.size];
        System.arraycopy(camos, offset, array, 0, size);
        System.arraycopy(other.camos, other.offset, array, size, other.size);
        return new CamoList(array);
    }

    @Override
    public Iterator<CamoContainer<?, ?>> iterator() {
        return ObjectIterators.wrap(camos, offset, size);
    }

    @Override
    public Spliterator<CamoContainer<?, ?>> spliterator() {
        return ObjectSpliterators.wrap(camos, offset, size);
    }

    @Override
    public void forEach(Consumer<? super CamoContainer<?, ?>> action) {
        int end = offset + size;
        for (int i = offset; i < end; i++) {
            action.accept(camos[i]);
        }
    }

    public Stream<CamoContainer<?, ?>> stream() {
        return Arrays.stream(camos, offset, offset + size);
    }

    public CamoList reversed() {
        return switch (size) {
            case 0 -> EMPTY;
            case 1 -> this;
            default -> new CamoList(ObjectArrays.reverse(ObjectArrays.copy(camos, offset, size)));
        };
    }

    public CamoList subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex) {
            throw new IllegalArgumentException("Invalid indizes");
        }
        if (fromIndex >= size) {
            return EMPTY;
        }
        return new CamoList(camos, fromIndex + offset, Math.min(toIndex, size) + offset);
    }

    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof CamoList otherList && Arrays.equals(
                camos, offset, offset + size,
                otherList.camos, otherList.offset, otherList.offset + otherList.size
        ));
    }

    @Override
    public int hashCode() {
        int result = 1;
        int end = offset + size;
        for (int i = offset; i < end; i++) {
            result = 31 * result + camos[i].hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "CamoList[]";
        }

        StringBuilder builder = new StringBuilder("CamoList[");
        int end = offset + size;
        for (int i = offset; i < end; i++) {
            if (i > offset) {
                builder.append(", ");
            }
            builder.append(camos[i]);
        }
        return builder.append("]").toString();
    }

    private CamoContainer<?, ?>[] getCamosForSerialization() {
        if (offset > 0 || size < camos.length) {
            return Arrays.copyOfRange(camos, offset, offset + size);
        }
        return camos;
    }

    public static CamoList of(CamoContainer<?, ?> camo) {
        return camo.isEmpty() ? EMPTY : new CamoList(new CamoContainer[] { camo });
    }

    public static CamoList of(CamoContainer<?, ?>... camos) {
        return camos.length == 0 ? EMPTY : new CamoList(Arrays.copyOf(camos, camos.length));
    }

    public static CamoList of(List<CamoContainer<?, ?>> camos) {
        return camos.isEmpty() ? EMPTY : new CamoList(camos.toArray(CamoContainer[]::new));
    }

    public static CamoList get(ItemStack stack) {
        return stack.getOrDefault(FramedConstants.Objects.DC_TYPE_CAMO_LIST, EMPTY);
    }
}
