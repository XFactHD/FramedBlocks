package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class DeferredParticleType<T extends ParticleOptions> extends DeferredHolder<ParticleType<?>, ParticleType<T>> {
    private DeferredParticleType(ResourceKey<ParticleType<?>> key) {
        super(key);
    }

    public static <T extends ParticleOptions> DeferredParticleType<T> createParticleType(Identifier name) {
        return createParticleType(ResourceKey.create(Registries.PARTICLE_TYPE, name));
    }

    public static <T extends ParticleOptions> DeferredParticleType<T> createParticleType(ResourceKey<ParticleType<?>> key) {
        return new DeferredParticleType<>(key);
    }
}
