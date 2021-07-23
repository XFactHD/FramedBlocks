package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.ChestState;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.*;

public class FramedChestModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_chest_lock");

    private final boolean closed;

    public FramedChestModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.closed = state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED;
    }

    public FramedChestModel(BakedModel baseModel) { this(FBContent.blockFramedChest.get().defaultBlockState(), baseModel); }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 1F/16F, 1F/16F, 15F/16F, 15F/16F))
            {
                if (topBotQuad.getDirection() == Direction.UP)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, closed ? 14F/16F : 10F/16F);
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
            if (BakedQuadTransformer.createSideQuad(sideQuad, 1F/16F, 0, 15F/16F, closed ? 14F/16F : 10F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 15F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data)
    {
        if (!closed) { return; }

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