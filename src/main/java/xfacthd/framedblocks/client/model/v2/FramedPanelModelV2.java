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

public class FramedPanelModelV2 extends FramedBlockModelV2
{
    private final Direction dir;

    public FramedPanelModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == dir.getOpposite())
        {
            BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
            BakedQuadTransformer.setQuadPosInFacingDir(frontQuad, .5F);
            quadMap.get(null).add(frontQuad);
        }
        else if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            boolean dirPositive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dirPositive, .5F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
        }
    }
}