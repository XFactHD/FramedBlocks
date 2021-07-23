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

public class FramedSlabCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(PropertyHolder.FACING_HOR);
        top = state.getValue(PropertyHolder.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == dir || quad.getDirection() == dir.getOpposite())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getClockWise(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F)
            )
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
        else if (quad.getDirection() == dir.getClockWise() || quad.getDirection() == dir.getCounterClockWise())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F)
            )
            {
                if (quad.getDirection() == dir.getCounterClockWise())
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
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), .5F)
            )
            {
                if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                }
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
    }
}