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

public class FramedSlabModelV2 extends FramedBlockModelV2
{
    private final boolean top;

    public FramedSlabModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        top = state.get(PropertyHolder.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, final BakedQuad quad)
    {
        if ((top && quad.getFace() == Direction.DOWN) || (!top && quad.getFace() == Direction.UP))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
            quadMap.get(null).add(topBotQuad);
        }
        else if (quad.getFace().getAxis() != Direction.Axis.Y)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
        }
    }
}