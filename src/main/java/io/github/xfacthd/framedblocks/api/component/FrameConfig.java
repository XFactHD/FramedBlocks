package io.github.xfacthd.framedblocks.api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/// Describes the modifier configuration of a framed block.
///
/// @param glowing    Whether the glowing modifier is applied
/// @param intangible Whether the intangibility modifier is applied
/// @param reinforced Whether the reinforcement modifier is applied
/// @param emissive   Whether the emissivity modifier is applied
public record FrameConfig(boolean glowing, boolean intangible, boolean reinforced, boolean emissive) {
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
    /// Default config of a framed block with no modifiers.
    public static final FrameConfig DEFAULT = new FrameConfig(false, false, false, false);

    /// Collect the modifier configuration from the given BE and, if non-default, append it to the component map builder.
    ///
    /// @param builder The component map builder to append to
    /// @param be      The BE to collect the modifier config from
    public static void collect(DataComponentMap.Builder builder, IFramedBlockEntity be) {
        FrameConfig cfg = new FrameConfig(be.isGlowing(), be.isMarkedIntangible(), be.isReinforced(), be.isEmissive());
        if (!cfg.equals(FrameConfig.DEFAULT)) {
            builder.set(FramedConstants.Objects.DC_TYPE_FRAME_CONFIG, cfg);
        }
    }

    /// Apply this modifier configuration to the given BE.
    ///
    /// @param be The BE to apply the modifier config to
    public void apply(IFramedBlockEntity be) {
        be.setGlowing(glowing);
        be.setIntangible(intangible);
        be.setReinforced(reinforced);
        be.setEmissive(emissive);
    }
}
