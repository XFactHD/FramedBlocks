package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedSlabEdgeModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedSlabEdgeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(PropertyHolder.FACING_HOR);
        top = state.getValue(PropertyHolder.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, final BakedQuad quad)
    {
        if (quad.getDirection().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                if ((quad.getDirection() == Direction.UP) == top)
                {
                    quadMap.get(quad.getDirection()).add(topBotQuad);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
        }
        else if (quad.getDirection() == dir || quad.getDirection() == dir.getOpposite())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F))
            {
                if (quad.getDirection() == dir)
                {
                    quadMap.get(quad.getDirection()).add(sideQuad);
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
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
        }
    }
}