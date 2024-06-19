package xfacthd.framedblocks.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.common.particle.FluidParticleOptions;

public final class FluidSpriteParticle extends TextureSheetParticle
{
    private final BlockPos pos;
    private final float uo;
    private final float vo;
    private final int brightness;

    @SuppressWarnings("deprecation")
    public FluidSpriteParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, Fluid fluid)
    {
        super(level, x, y, z, sx, sy, sz);
        this.pos = BlockPos.containing(x, y, z);
        this.gravity = 1F;
        this.quadSize /= 2F;
        this.uo = random.nextFloat() * 3F;
        this.vo = random.nextFloat() * 3F;
        this.brightness = fluid.getFluidType().getLightLevel(fluid.defaultFluidState(), level, pos);

        int tint = ClientUtils.getFluidColor(level, pos, fluid.defaultFluidState());
        this.rCol = .6F * (float)(tint >> 16 & 0xFF) / 255F;
        this.gCol = .6F * (float)(tint >>  8 & 0xFF) / 255F;
        this.bCol = .6F * (float)(tint       & 0xFF) / 255F;

        ResourceLocation stillTex = IClientFluidTypeExtensions.of(fluid).getStillTexture();
        setSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(stillTex));
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0()
    {
        return sprite.getU((uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1()
    {
        return sprite.getU(uo / 4.0F);
    }

    @Override
    protected float getV0()
    {
        return sprite.getV(vo / 4.0F);
    }

    @Override
    protected float getV1()
    {
        return sprite.getV((vo + 1.0F) / 4.0F);
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
        public Particle createParticle(FluidParticleOptions options, ClientLevel level, double x, double y, double z, double sx, double sy, double sz)
        {
            if (options.fluid() != Fluids.EMPTY)
            {
                return new FluidSpriteParticle(level, x, y, z, sx, sy, sz, options.fluid());
            }
            return null;
        }
    }
}
