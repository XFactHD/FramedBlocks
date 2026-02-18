package io.github.xfacthd.framedblocks.common.particle;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BlockOverlayParticleOptions(Holder<BlockOverlay> overlay) implements ParticleOptions
{
    public static final MapCodec<BlockOverlayParticleOptions> CODEC = BlockOverlay.CODEC
            .xmap(BlockOverlayParticleOptions::new, BlockOverlayParticleOptions::overlay)
            .fieldOf("fluid");
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockOverlayParticleOptions> STREAM_CODEC =
            BlockOverlay.STREAM_CODEC.map(BlockOverlayParticleOptions::new, BlockOverlayParticleOptions::overlay);

    @Override
    public ParticleType<?> getType()
    {
        return FBContent.BLOCK_OVERLAY_PARTICLE.value();
    }
}
