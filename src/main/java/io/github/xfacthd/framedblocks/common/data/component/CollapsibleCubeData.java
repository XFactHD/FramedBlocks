package io.github.xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CollapsibleCubeData(int offsets) {
    public static final Codec<CollapsibleCubeData> CODEC = Codec.INT
            .xmap(CollapsibleCubeData::new, CollapsibleCubeData::offsets);
    public static final StreamCodec<ByteBuf, CollapsibleCubeData> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(CollapsibleCubeData::new, CollapsibleCubeData::offsets);
    public static final CollapsibleCubeData EMPTY = new CollapsibleCubeData(0);
}
