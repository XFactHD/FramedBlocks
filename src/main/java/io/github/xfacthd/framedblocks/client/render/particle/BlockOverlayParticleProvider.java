package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.overlay.TintSource;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.common.particle.BlockOverlayParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class BlockOverlayParticleProvider implements ParticleProvider<BlockOverlayParticleOptions> {
    public static Particle createParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, BlockPos pos, BlockOverlay overlay) {
        int tintColor = -1;
        TintSource tintSource = overlay.tintSource();
        if (tintSource != null) {
            tintColor = TintUtils.getOverlayTintSource(tintSource).colorInWorld(tintSource.defaultBlockState(), level, pos);
        }
        return new BlockAtlasSpriteParticle(level, x, y, z, sx, sy, sz, pos, overlay.solidTexture(), tintColor);
    }

    @Override
    public Particle createParticle(BlockOverlayParticleOptions options, ClientLevel level, double x, double y, double z, double sx, double sy, double sz, RandomSource random) {
        return createParticle(level, x, y, z, sx, sy, sz, BlockPos.containing(x, y, z), options.overlay().value());
    }
}
