package io.github.xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CollapsibleBlockData(int offsets) {
    public static final Codec<CollapsibleBlockData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("offsets").forGetter(CollapsibleBlockData::offsets)
    ).apply(inst, CollapsibleBlockData::new));
    public static final StreamCodec<FriendlyByteBuf, CollapsibleBlockData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CollapsibleBlockData::offsets,
            CollapsibleBlockData::new
    );
    public static final CollapsibleBlockData EMPTY = new CollapsibleBlockData(0);
}
