package xfacthd.framedblocks.client.model.torch;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelCache;

public class FramedSoulTorchGeometry extends FramedTorchGeometry
{
    public FramedSoulTorchGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.SOUL_TORCH.defaultBlockState(), rand, ModelData.EMPTY);
    }
}