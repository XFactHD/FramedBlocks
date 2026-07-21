package io.github.xfacthd.framedblocks.api.render.particle;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/// Particle options used for server-driven spawning of particles for a camo applied to a block.
public record CamoParticleOptions(CamoContainer<?, ?> camo) implements ParticleOptions {
    public static final MapCodec<CamoParticleOptions> CODEC = CamoContainerHelper.CODEC.fieldOf("camo")
            .xmap(CamoParticleOptions::new, CamoParticleOptions::camo);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoParticleOptions> STREAM_CODEC = CamoContainerHelper.STREAM_CODEC
            .map(CamoParticleOptions::new, CamoParticleOptions::camo);

    @Override
    public ParticleType<?> getType() {
        return FramedConstants.Objects.PARTICLE_TYPE_CAMO.value();
    }
}
