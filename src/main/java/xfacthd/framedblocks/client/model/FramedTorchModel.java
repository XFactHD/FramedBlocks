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
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.*;

import java.util.*;

public class FramedTorchModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedConstants.MOD_ID, "block/framed_torch");

    public FramedTorchModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.TORCH.defaultBlockState(), rand, ModelData.EMPTY);
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

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (Utils.isY(quad.getDirection()))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 7F/16F, 7F/16F, 9F/16F, 9F/16F))
            {
                if (quad.getDirection() == Direction.UP)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 8F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
                else
                {
                    quadMap.get(quad.getDirection()).add(topBotQuad);
                }
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, 0F, 9F/16F, 8F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }
}