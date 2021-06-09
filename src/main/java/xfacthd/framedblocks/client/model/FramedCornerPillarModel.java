package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedCornerPillarModel extends FramedBlockModel
{
    private final Direction dir;

    public FramedCornerPillarModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.rotateY(), .5F))
            {
                if (quad.getFace() == dir)
                {
                    quadMap.get(quad.getFace()).add(sideQuad);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F))
            {
                if (quad.getFace() == dir.rotateYCCW())
                {
                    quadMap.get(quad.getFace()).add(sideQuad);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), .5F)
            )
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }
        }
    }
}