package xfacthd.framedblocks.api.camo.empty;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.camo.CamoClientHandler;
import xfacthd.framedblocks.api.model.util.ModelUtils;

final class EmptyCamoClientHandler extends CamoClientHandler<EmptyCamoContent>
{
    static final CamoClientHandler<EmptyCamoContent> INSTANCE = new EmptyCamoClientHandler();

    private EmptyCamoClientHandler() { }

    @Override
    public ChunkRenderTypeSet getRenderTypes(EmptyCamoContent camo, RandomSource random, ModelData data)
    {
        return ModelUtils.CUTOUT;
    }

    @Override
    public BakedModel getOrCreateModel(EmptyCamoContent camo)
    {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(FramedBlocksAPI.INSTANCE.getDefaultModelState());
    }

    @Override
    public Particle makeHitDestroyParticle(
            ClientLevel level, double x, double y, double z, double sx, double sy, double sz, EmptyCamoContent camo, BlockPos pos
    )
    {
        BlockState state = FramedBlocksAPI.INSTANCE.getDefaultModelState();
        return new TerrainParticle(level, x, y, z, 0.0D, 0.0D, 0.0D, state, pos);
    }
}
