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

public class FramedThreewayCornerModelV2 extends FramedBlockModelV2
{
    private final Direction dir;
    private final boolean top;

    public FramedThreewayCornerModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        top = state.get(PropertyHolder.TOP);
    }

    public FramedThreewayCornerModelV2(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedThreewayCorner.getDefaultState().with(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if ((quad.getFace() == Direction.UP && top) || (quad.getFace() == Direction.DOWN && !top))
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir))
            {
                quadMap.get(quad.getFace()).add(triQuad);
            }
        }
        else if (quad.getFace() == dir || quad.getFace() == dir.rotateYCCW())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getFace() == dir, top))
            {
                quadMap.get(quad.getFace()).add(triQuad);
            }
        }
        else if (quad.getFace() == dir.getOpposite())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSmallTriangleQuad(triQuad, TriangleDirection.RIGHT))
            {
                BakedQuadTransformer.createTopBottomSlopeQuad(triQuad, !top);
                quadMap.get(null).add(triQuad);
            }

            triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSmallTriangleQuad(triQuad, top ? TriangleDirection.DOWN : TriangleDirection.UP))
            {
                BakedQuadTransformer.createSideSlopeQuad(triQuad, true);
                quadMap.get(null).add(triQuad);
            }
        }
        else if (quad.getFace() == dir.rotateY())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSmallTriangleQuad(triQuad, TriangleDirection.LEFT))
            {
                BakedQuadTransformer.createTopBottomSlopeQuad(triQuad, !top);
                quadMap.get(null).add(triQuad);
            }
        }
    }
}