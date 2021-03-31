package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedInnerThreewayCornerModelV2 extends FramedBlockModelV2
{
    private final Direction dir;
    private final boolean top;

    public FramedInnerThreewayCornerModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        top = state.get(PropertyHolder.TOP);
    }

    public FramedInnerThreewayCornerModelV2(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedInnerThreewayCorner.getDefaultState().with(PropertyHolder.FACING_HOR, Direction.EAST),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if ((quad.getFace() == Direction.DOWN && top) || (quad.getFace() == Direction.UP && !top))
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir.rotateY()))
            {
                quadMap.get(quad.getFace()).add(triQuad);
            }
        }
        else if (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getFace() == dir.getOpposite(), top))
            {
                quadMap.get(quad.getFace()).add(triQuad);
            }

            if (quad.getFace() == dir.getOpposite())
            {
                triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSmallTriangleQuad(triQuad, TriangleDirection.RIGHT))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(triQuad, !top);
                    quadMap.get(null).add(triQuad);
                }

                triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSmallTriangleQuad(triQuad, top ? TriangleDirection.UP : TriangleDirection.DOWN))
                {
                    BakedQuadTransformer.createSideSlopeQuad(triQuad, false);
                    quadMap.get(null).add(triQuad);
                }
            }
            else if (quad.getFace() == dir.rotateYCCW())
            {
                triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSmallTriangleQuad(triQuad, TriangleDirection.LEFT))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(triQuad, !top);
                    quadMap.get(null).add(triQuad);
                }
            }
        }
    }
}