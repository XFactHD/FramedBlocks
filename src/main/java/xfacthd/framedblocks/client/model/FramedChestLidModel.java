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

public class FramedChestLidModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_chest_lock");

    public FramedChestLidModel(BlockState state, IBakedModel baseModel) { super(state, baseModel); }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 1F/16F, 1F/16F, 15F/16F, 15F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 14F/16F : 7F/16F);
                quadMap.get(null).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 1F/16F, 9F/16F, 15F/16F, 14F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 15F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data);
        for (BakedQuad quad : quads)
        {
            if (quad.getSprite().getName().equals(TEXTURE))
            {
                quadMap.get(null).add(quad);
            }
        }
    }
}