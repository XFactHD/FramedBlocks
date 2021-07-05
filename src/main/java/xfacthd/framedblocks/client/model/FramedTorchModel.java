package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.*;

public class FramedTorchModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_torch");

    public FramedTorchModel(BlockState state, IBakedModel baseModel) { super(state, baseModel); }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData extraData)
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

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection().getAxis() == Direction.Axis.Y)
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