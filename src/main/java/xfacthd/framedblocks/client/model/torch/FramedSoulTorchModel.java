package xfacthd.framedblocks.client.model.torch;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.util.ModelCache;

public class FramedSoulTorchModel extends FramedTorchModel
{
    public FramedSoulTorchModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.SOUL_TORCH.defaultBlockState(), rand, ModelData.EMPTY);
    }
}