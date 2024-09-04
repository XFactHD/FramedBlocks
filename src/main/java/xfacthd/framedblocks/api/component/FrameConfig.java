package xfacthd.framedblocks.api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;

public record FrameConfig(boolean glowing, boolean intangible, boolean reinforced)
{
    public static final Codec<FrameConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("glowing").forGetter(FrameConfig::glowing),
            Codec.BOOL.fieldOf("intangible").forGetter(FrameConfig::intangible),
            Codec.BOOL.fieldOf("reinforced").forGetter(FrameConfig::reinforced)
    ).apply(inst, FrameConfig::new));
    public static final StreamCodec<ByteBuf, FrameConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            FrameConfig::glowing,
            ByteBufCodecs.BOOL,
            FrameConfig::intangible,
            ByteBufCodecs.BOOL,
            FrameConfig::reinforced,
            FrameConfig::new
    );
    public static final FrameConfig DEFAULT = new FrameConfig(false, false, false);

    public void apply(FramedBlockEntity be)
    {
        be.setGlowing(glowing);
        be.setIntangible(intangible);
        be.setReinforced(reinforced);
    }
}
