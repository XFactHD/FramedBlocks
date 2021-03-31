package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedSlabEdgeModel extends FramedBlockModelV2
{
    private final Direction dir;
    private final boolean top;

    public FramedSlabEdgeModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        top = state.get(PropertyHolder.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, final BakedQuad quad)
    {
        if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                if ((quad.getFace() == Direction.UP) == top)
                {
                    quadMap.get(quad.getFace()).add(topBotQuad);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
        }
        else if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F))
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
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F) &&
                BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F)
            )
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
        }
    }
}