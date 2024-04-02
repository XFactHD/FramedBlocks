package xfacthd.framedblocks.api.camo.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.camo.CamoClientHandler;

final class BlockCamoClientHandler extends CamoClientHandler<BlockCamoContent>
{
    static final CamoClientHandler<BlockCamoContent> INSTANCE = new BlockCamoClientHandler();

    private BlockCamoClientHandler() { }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockCamoContent camo, RandomSource random, ModelData data)
    {
        return getOrCreateModel(camo).getRenderTypes(camo.getState(), random, data);
    }

    @Override
    public BakedModel getOrCreateModel(BlockCamoContent camo)
    {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(camo.getState());
    }

    @Override
    public Particle makeHitDestroyParticle(
            ClientLevel level, double x, double y, double z, double sx, double sy, double sz, BlockCamoContent camo, BlockPos pos
    )
    {
        return new TerrainParticle(level, x, y, z, 0.0D, 0.0D, 0.0D, camo.getState(), pos);
    }
}
