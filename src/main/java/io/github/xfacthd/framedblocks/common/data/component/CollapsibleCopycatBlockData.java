package io.github.xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CollapsibleCopycatBlockData(int offsets) {
    public static final Codec<CollapsibleCopycatBlockData> CODEC = Codec.INT
            .xmap(CollapsibleCopycatBlockData::new, CollapsibleCopycatBlockData::offsets);
    public static final StreamCodec<ByteBuf, CollapsibleCopycatBlockData> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(CollapsibleCopycatBlockData::new, CollapsibleCopycatBlockData::offsets);
    public static final CollapsibleCopycatBlockData EMPTY = new CollapsibleCopycatBlockData(0);
}
