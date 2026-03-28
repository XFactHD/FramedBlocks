package io.github.xfacthd.framedblocks.api.util.network;

import net.minecraft.network.codec.StreamCodec;

import java.util.function.UnaryOperator;

public record ValidatingDecoder<B, V>(StreamCodec<B, V> wrapped, UnaryOperator<V> validator) implements StreamCodec<B, V> {
    @Override
    public void encode(B buffer, V value) {
        wrapped.encode(buffer, value);
    }

    @Override
    public V decode(B buffer) {
        return validator.apply(wrapped.decode(buffer));
    }

    public static <B, V> CodecOperation<B, V, V> of(UnaryOperator<V> validator) {
        return codec -> new ValidatingDecoder<>(codec, validator);
    }
}
