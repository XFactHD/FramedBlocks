package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.common.particle.FluidParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public final class FluidSpriteParticle extends BlockAtlasSpriteParticle {
    private final int brightness;

    public FluidSpriteParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, Fluid fluid) {
        super(level, x, y, z, sx, sy, sz, ModelUtils.getFluidModel(fluid.defaultFluidState()).stillMaterial().sprite());
        this.brightness = fluid.getFluidType().getLightLevel(fluid.defaultFluidState(), level, pos);

        int tint = TintUtils.getFluidColor(level, pos, fluid.defaultFluidState());
        this.rCol = .6F * (float)(tint >> 16 & 0xFF) / 255F;
        this.gCol = .6F * (float)(tint >>  8 & 0xFF) / 255F;
        this.bCol = .6F * (float)(tint       & 0xFF) / 255F;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected int getLightCoords(float partialTick) {
        int light = level.hasChunkAt(pos) ? LevelRenderer.getLightCoords(level, pos) : 0;
        int block = Math.max(brightness, LightCoordsUtil.block(light));
        return LightCoordsUtil.pack(block, LightCoordsUtil.sky(light));
    }

    public static final class Provider implements ParticleProvider<FluidParticleOptions> {
        @Override
        public @Nullable Particle createParticle(
                FluidParticleOptions options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double sx,
                double sy,
                double sz,
                RandomSource random
        ) {
            if (options.fluid() != Fluids.EMPTY) {
                return new FluidSpriteParticle(level, x, y, z, sx, sy, sz, options.fluid());
            }
            return null;
        }
    }
}
