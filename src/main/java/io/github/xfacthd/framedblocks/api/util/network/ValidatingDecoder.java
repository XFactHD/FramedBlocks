package io.github.xfacthd.framedblocks.api.util.network;

import net.minecraft.network.codec.StreamCodec;

import java.util.function.UnaryOperator;

/// Wrapping stream codec which applies the given validator after decoding a value with the given stream codec.
/// The validator is expected to return the decoded value if it is valid and an applicable fallback value if
/// the decoded is not valid.
///
/// @param wrapped   The stream codec to use for encoding/decoding the value
/// @param validator The validator to apply to the decoded value
public record ValidatingDecoder<B, V>(StreamCodec<B, V> wrapped, UnaryOperator<V> validator) implements StreamCodec<B, V> {
    @Override
    public void encode(B buffer, V value) {
        wrapped.encode(buffer, value);
    }

    @Override
    public V decode(B buffer) {
        return validator.apply(wrapped.decode(buffer));
    }

    /// {@return a codec operation applying the given validator to a stream codec}
    ///
    /// @param validator The validator to apply
    public static <B, V> CodecOperation<B, V, V> of(UnaryOperator<V> validator) {
        return codec -> new ValidatingDecoder<>(codec, validator);
    }
}
