package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedInnerThreewayCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedInnerThreewayCornerModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(PropertyHolder.FACING_HOR);
        top = state.getValue(PropertyHolder.TOP);
    }

    public FramedInnerThreewayCornerModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedInnerThreewayCorner.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.EAST),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if ((quad.getDirection() == Direction.DOWN && top) || (quad.getDirection() == Direction.UP && !top))
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir.getClockWise()))
            {
                quadMap.get(quad.getDirection()).add(triQuad);
            }
        }
        else if (quad.getDirection() == dir.getOpposite() || quad.getDirection() == dir.getCounterClockWise())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getDirection() == dir.getOpposite(), top))
            {
                quadMap.get(quad.getDirection()).add(triQuad);
            }

            if (quad.getDirection() == dir.getOpposite())
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
            else if (quad.getDirection() == dir.getCounterClockWise())
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