package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.render.particle.CamoParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class CamoParticleProvider implements ParticleProvider<CamoParticleOptions> {
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Particle createParticle(CamoParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
        BlockPos pos = BlockPos.containing(x, y, z);
        CamoContainer camo = options.camo();
        CamoContent content = camo.getContent();

        int tintColor = camo.getClientHandler().getParticleTintValue(camo, level, pos);
        return content.getClientHandler().createParticle(level, x, y, z, xAux, yAux, zAux, pos, content, tintColor);
    }
}
