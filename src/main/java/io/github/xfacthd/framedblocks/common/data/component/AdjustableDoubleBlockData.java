package io.github.xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AdjustableDoubleBlockData(int firstHeight) {
    public static final Codec<AdjustableDoubleBlockData> CODEC = Codec.INT
            .xmap(AdjustableDoubleBlockData::new, AdjustableDoubleBlockData::firstHeight);
    public static final StreamCodec<ByteBuf, AdjustableDoubleBlockData> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(AdjustableDoubleBlockData::new, AdjustableDoubleBlockData::firstHeight);
    public static final AdjustableDoubleBlockData EMPTY = new AdjustableDoubleBlockData(0);
}
