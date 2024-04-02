package xfacthd.framedblocks.api.camo;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

public abstract class CamoClientHandler<T extends CamoContent<T>>
{
    /**
     * {@return the {@link ChunkRenderTypeSet} which the given {@link CamoContent} renders in}
     */
    public abstract ChunkRenderTypeSet getRenderTypes(T camo, RandomSource random, ModelData data);

    /**
     * {@return the {@link BakedModel} to be rendered for the given {@link CamoContent}}
     * @implNote this method must be backed by a cache
     */
    public abstract BakedModel getOrCreateModel(T camo);

    /**
     * {@return a {@link Particle} to be spawned when a block with the given {@link CamoContent} is punched or broken}
     */
    public abstract Particle makeHitDestroyParticle(
            ClientLevel level, double x, double y, double z, double sx, double sy, double sz, T camo, BlockPos pos
    );
}
