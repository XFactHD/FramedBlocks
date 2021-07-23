package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;

import java.util.*;

public class FramedSoulWallTorchModel extends FramedWallTorchModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_soul_torch");

    public FramedSoulWallTorchModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

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
}