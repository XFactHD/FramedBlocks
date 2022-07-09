package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.client.ModelCache;

import java.util.*;

public class FramedSoulTorchModel extends FramedTorchModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedConstants.MOD_ID, "block/framed_soul_torch");

    public FramedSoulTorchModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.SOUL_TORCH.defaultBlockState(), rand, ModelData.EMPTY);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData extraData, RenderType layer)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, extraData, layer);
        for (BakedQuad quad : quads)
        {
            if (!quad.getSprite().getName().equals(TEXTURE))
            {
                quadMap.get(null).add(quad);
            }
        }
    }
}