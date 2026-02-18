package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.common.particle.BlockOverlayParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockOverlayParticle extends BlockAtlasSpriteParticle
{
    public BlockOverlayParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockOverlay overlay)
    {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, overlay.solidTexture());

        if (overlay.tintSource() != null)
        {
            BlockState tintSourceState = overlay.tintSource().value().defaultBlockState();
            int tint = ClientUtils.getBlockColor(level, pos, tintSourceState, 1);
            this.rCol = .6F * (float) (tint >> 16 & 0xFF) / 255F;
            this.gCol = .6F * (float) (tint >> 8 & 0xFF) / 255F;
            this.bCol = .6F * (float) (tint & 0xFF) / 255F;
        }
    }

    public static final class Provider implements ParticleProvider<BlockOverlayParticleOptions>
    {
        @Override
        public Particle createParticle(
                BlockOverlayParticleOptions options,
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
            return new BlockOverlayParticle(level, x, y, z, sx, sy, sz, options.overlay().value());
        }
    }
}
