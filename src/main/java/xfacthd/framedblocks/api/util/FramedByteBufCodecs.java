package xfacthd.framedblocks.api.util;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.network.VarLong;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.IntFunction;

public final class FramedByteBufCodecs
{
    public static <B extends ByteBuf, V> StreamCodec<B, V[]> array(StreamCodec<? super B, V> wrapped, IntFunction<V[]> arrayFactory, int maxSize)
    {
        return new StreamCodec<>()
        {
            @Override
            public void encode(B buf, V[] arr)
            {
                ByteBufCodecs.writeCount(buf, arr.length, maxSize);
                for (V v : arr)
                {
                    wrapped.encode(buf, v);
                }
            }

            @Override
            public V[] decode(B buf)
            {
                int size = ByteBufCodecs.readCount(buf, maxSize);
                V[] arr = arrayFactory.apply(size);
                for (int i = 0; i < size; i++)
                {
                    arr[i] = wrapped.decode(buf);
                }
                return arr;
            }
        };
    }

    public static <B extends ByteBuf, C extends LongCollection> StreamCodec<B, C> longCollection(
            IntFunction<C> collectionFactory
    )
    {
        return longCollection(collectionFactory, Integer.MAX_VALUE);
    }

    public static <B extends ByteBuf, C extends LongCollection> StreamCodec<B, C> longCollection(
            IntFunction<C> collectionFactory, int maxSize
    )
    {
        return new StreamCodec<>()
        {
            @Override
            public C decode(B buf)
            {
                int size = ByteBufCodecs.readCount(buf, maxSize);
                C collection = collectionFactory.apply(size);
                for (int i = 0; i < size; i++)
                {
                    collection.add(VarLong.read(buf));
                }
                return collection;
            }

            @Override
            public void encode(B buf, C collection)
            {
                ByteBufCodecs.writeCount(buf, collection.size(), maxSize);
                collection.forEach((long value) -> VarLong.write(buf, value));
            }
        };
    }



    private FramedByteBufCodecs() { }
}
