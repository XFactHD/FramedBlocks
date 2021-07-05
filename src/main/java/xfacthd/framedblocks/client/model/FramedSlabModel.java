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

public class FramedSlabModel extends FramedBlockModel
{
    private final boolean top;

    public FramedSlabModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        top = state.getValue(PropertyHolder.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, final BakedQuad quad)
    {
        if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
            quadMap.get(null).add(topBotQuad);
        }
        else if (quad.getDirection().getAxis() != Direction.Axis.Y)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
        }
    }
}