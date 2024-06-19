package xfacthd.framedblocks.common.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class BasicParticleType<O extends ParticleOptions> extends ParticleType<O>
{
    private final MapCodec<O> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, O> streamCodec;

    public BasicParticleType(boolean overrideLimitter, MapCodec<O> codec, StreamCodec<? super RegistryFriendlyByteBuf, O> streamCodec)
    {
        super(overrideLimitter);
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    @Override
    public MapCodec<O> codec()
    {
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, O> streamCodec()
    {
        return streamCodec;
    }
}
