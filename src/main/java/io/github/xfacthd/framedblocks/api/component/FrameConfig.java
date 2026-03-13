package io.github.xfacthd.framedblocks.api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FrameConfig(boolean glowing, boolean intangible, boolean reinforced, boolean emissive)
{
    public static final Codec<FrameConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("glowing").forGetter(FrameConfig::glowing),
            Codec.BOOL.fieldOf("intangible").forGetter(FrameConfig::intangible),
            Codec.BOOL.fieldOf("reinforced").forGetter(FrameConfig::reinforced),
            Codec.BOOL.fieldOf("emissive").forGetter(FrameConfig::emissive)
    ).apply(inst, FrameConfig::new));
    public static final StreamCodec<ByteBuf, FrameConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            FrameConfig::glowing,
            ByteBufCodecs.BOOL,
            FrameConfig::intangible,
            ByteBufCodecs.BOOL,
            FrameConfig::reinforced,
            ByteBufCodecs.BOOL,
            FrameConfig::emissive,
            FrameConfig::new
    );
    public static final FrameConfig DEFAULT = new FrameConfig(false, false, false, false);

    public static void collect(DataComponentMap.Builder builder, IFramedBlockEntity be)
    {
        FrameConfig cfg = new FrameConfig(be.isGlowing(), be.isMarkedIntangible(), be.isReinforced(), be.isEmissive());
        if (!cfg.equals(FrameConfig.DEFAULT))
        {
            builder.set(Utils.DC_TYPE_FRAME_CONFIG, cfg);
        }
    }

    public void apply(IFramedBlockEntity be)
    {
        be.setGlowing(glowing);
        be.setIntangible(intangible);
        be.setReinforced(reinforced);
        be.setEmissive(emissive);
    }
}
