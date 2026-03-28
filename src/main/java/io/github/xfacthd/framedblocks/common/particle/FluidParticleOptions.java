package io.github.xfacthd.framedblocks.common.particle;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

public record FluidParticleOptions(Fluid fluid) implements ParticleOptions {
    public static final MapCodec<FluidParticleOptions> CODEC = BuiltInRegistries.FLUID.byNameCodec()
            .xmap(FluidParticleOptions::new, FluidParticleOptions::fluid)
            .fieldOf("fluid");
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidParticleOptions> STREAM_CODEC =
            ByteBufCodecs.registry(Registries.FLUID).map(FluidParticleOptions::new, FluidParticleOptions::fluid);

    @Override
    public ParticleType<?> getType() {
        return FBContent.FLUID_PARTICLE.get();
    }
}
