package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.common.particle.FluidParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jspecify.annotations.Nullable;

public final class FluidSpriteParticle extends BlockAtlasSpriteParticle
{
    private final int brightness;

    public FluidSpriteParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, Fluid fluid)
    {
        super(level, x, y, z, sx, sy, sz, IClientFluidTypeExtensions.of(fluid).getStillTexture());
        this.brightness = fluid.getFluidType().getLightLevel(fluid.defaultFluidState(), level, pos);

        int tint = ClientUtils.getFluidColor(level, pos, fluid.defaultFluidState());
        this.rCol = .6F * (float)(tint >> 16 & 0xFF) / 255F;
        this.gCol = .6F * (float)(tint >>  8 & 0xFF) / 255F;
        this.bCol = .6F * (float)(tint       & 0xFF) / 255F;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightColor(float partialTick)
    {
        int light = level.hasChunkAt(pos) ? LevelRenderer.getLightColor(level, pos) : 0;
        int block = Math.max(brightness, LightTexture.block(light));
        return LightTexture.pack(block, LightTexture.sky(light));
    }

    public static final class Provider implements ParticleProvider<FluidParticleOptions>
    {
        @Nullable
        @Override
        public Particle createParticle(
                FluidParticleOptions options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double sx,
                double sy,
                double sz,
                RandomSource random
        )
        {
            if (options.fluid() != Fluids.EMPTY)
            {
                return new FluidSpriteParticle(level, x, y, z, sx, sy, sz, options.fluid());
            }
            return null;
        }
    }
}
