package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedVerticalHalfStairsModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedVerticalHalfStairsModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(PropertyHolder.FACING_HOR);
        this.top = state.getValue(PropertyHolder.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == dir.getOpposite() || quad.getDirection() == dir.getClockWise())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getDirection() == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite(), .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F)
            )
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getDirection() == dir.getOpposite() ? dir.getCounterClockWise() : dir, .5F) &&
                BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, .5F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if (quad.getDirection() == dir || quad.getDirection() == dir.getCounterClockWise())
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(sideQuad, top, .5F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
        }

        if (Utils.isY(quad.getDirection()))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                if (quad.getDirection() == Direction.UP != top)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                }
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }

            topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, .5F) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), .5F)
            )
            {
                if (quad.getDirection() == Direction.UP != top)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F);
                }
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
    }
}
