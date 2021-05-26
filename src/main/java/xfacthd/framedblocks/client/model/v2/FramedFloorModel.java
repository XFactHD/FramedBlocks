package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedFloorModel extends FramedBlockModelV2
{
    public FramedFloorModel(BlockState state, IBakedModel baseModel) { super(state, baseModel); }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == Direction.UP)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 1F/16F);
            quadMap.get(null).add(topBotQuad);
        }
        else if (quad.getFace().getAxis() != Direction.Axis.Y)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, false, 1F/16F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
        }
    }
}