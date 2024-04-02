package xfacthd.framedblocks.client.model.torch;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;

public class FramedSoulWallTorchGeometry extends FramedWallTorchGeometry
{
    public FramedSoulWallTorchGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelUtils.getRenderTypes(Blocks.SOUL_TORCH.defaultBlockState(), rand, ModelData.EMPTY);
    }
}