package io.github.xfacthd.framedblocks.common.util.registration;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.util.registration.DeferredParticleType;
import io.github.xfacthd.framedblocks.common.particle.BasicParticleType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DeferredParticleTypeRegister extends DeferredRegister<ParticleType<?>>
{
    private DeferredParticleTypeRegister(String namespace)
    {
        super(Registries.PARTICLE_TYPE, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends ParticleType<?>> DeferredHolder<ParticleType<?>, I> createHolder(
            ResourceKey<? extends Registry<ParticleType<?>>> registryKey, Identifier key
    )
    {
        return (DeferredHolder<ParticleType<?>, I>) DeferredParticleType.createParticleType(ResourceKey.create(registryKey, key));
    }

    public <O extends ParticleOptions> DeferredParticleType<O> registerParticleType(
            String name, boolean overrideLimiter, MapCodec<O> codec, StreamCodec<? super RegistryFriendlyByteBuf, O> streamCodec
    )
    {
        DeferredHolder<ParticleType<?>, ParticleType<O>> holder = register(name, () -> new BasicParticleType<>(overrideLimiter, codec, streamCodec));
        return (DeferredParticleType<O>) holder;
    }

    public static DeferredParticleTypeRegister create(String namespace)
    {
        return new DeferredParticleTypeRegister(namespace);
    }
}
