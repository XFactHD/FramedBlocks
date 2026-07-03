package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

/// Specialized deferred holder for [ParticleType]s.
public final class DeferredParticleType<T extends ParticleOptions> extends DeferredHolder<ParticleType<?>, ParticleType<T>> {
    private DeferredParticleType(ResourceKey<ParticleType<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the particle type registered under the given name}
    ///
    /// @param name The registry name of the particle type
    public static <T extends ParticleOptions> DeferredParticleType<T> createParticleType(Identifier name) {
        return createParticleType(ResourceKey.create(Registries.PARTICLE_TYPE, name));
    }

    /// {@return a deferred holder for the particle type registered under the given key}
    ///
    /// @param key The registry key of the particle type
    public static <T extends ParticleOptions> DeferredParticleType<T> createParticleType(ResourceKey<ParticleType<?>> key) {
        return new DeferredParticleType<>(key);
    }
}
