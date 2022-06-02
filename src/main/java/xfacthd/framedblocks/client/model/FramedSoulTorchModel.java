package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.util.FramedConstants;

import java.util.*;

public class FramedSoulTorchModel extends FramedTorchModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedConstants.MOD_ID, "block/framed_soul_torch");

    public FramedSoulTorchModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer)
    {
        return ItemBlockRenderTypes.canRenderInLayer(Blocks.SOUL_TORCH.defaultBlockState(), layer);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData extraData, RenderType layer)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, extraData);
        for (BakedQuad quad : quads)
        {
            if (!quad.getSprite().getName().equals(TEXTURE))
            {
                quadMap.get(null).add(quad);
            }
        }
    }
}