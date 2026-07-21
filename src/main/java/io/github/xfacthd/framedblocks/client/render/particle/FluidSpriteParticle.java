package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.material.Fluid;

public final class FluidSpriteParticle extends BlockAtlasSpriteParticle {
    private final int brightness;

    public FluidSpriteParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, BlockPos pos, Fluid fluid, int tintColor) {
        TextureAtlasSprite sprite = ModelUtils.getFluidModel(fluid.defaultFluidState()).stillMaterial().sprite();
        super(level, x, y, z, sx, sy, sz, pos, sprite, tintColor);
        this.brightness = fluid.getFluidType().getLightLevel(fluid.defaultFluidState(), level, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected int getLightCoords(float partialTick) {
        int light = level.hasChunkAt(pos) ? LightCoordsUtil.getLightCoords(level, pos) : 0;
        int block = Math.max(brightness, LightCoordsUtil.block(light));
        return LightCoordsUtil.pack(block, LightCoordsUtil.sky(light));
    }
}
