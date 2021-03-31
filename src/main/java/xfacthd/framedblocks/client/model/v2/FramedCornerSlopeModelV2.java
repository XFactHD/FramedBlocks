package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.CornerType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedCornerSlopeModelV2 extends FramedBlockModelV2
{
    private final Direction dir;
    private final CornerType type;

    public FramedCornerSlopeModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        type = state.get(PropertyHolder.CORNER_TYPE);
    }

    public FramedCornerSlopeModelV2(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedCornerSlope.getDefaultState().with(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type.isHorizontal())
        {
            if ((quad.getFace() == dir.rotateY() && type.isRight()) || (quad.getFace() == dir.rotateYCCW() && !type.isRight()))
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, type.isRight(), type.isTop()))
                {
                    quadMap.get(quad.getFace()).add(triQuad);
                }
            }
            else if ((quad.getFace() == dir.rotateYCCW() && type.isRight()) || (quad.getFace() == dir.rotateY() && !type.isRight()))
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(slopeQuad, !type.isRight(), type.isTop()))
                {
                    BakedQuadTransformer.createSideSlopeQuad(slopeQuad, type.isRight());
                    quadMap.get(null).add(slopeQuad);
                }
            }
            else if ((quad.getFace() == Direction.UP && type.isTop()) || (quad.getFace() == Direction.DOWN && !type.isTop()))
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, type.isRight() ? dir.rotateY() : dir))
                {
                    quadMap.get(quad.getFace()).add(triQuad);
                }
            }
            else if (quad.getFace() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(slopeQuad, type.isRight(), !type.isTop()))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slopeQuad, !type.isTop());
                    quadMap.get(null).add(slopeQuad);
                }
            }
        }
        else
        {
            if (quad.getFace() == dir)
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, true, type.isTop()))
                {
                    quadMap.get(quad.getFace()).add(triQuad);
                }
            }
            else if (quad.getFace() == dir.rotateYCCW())
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, false, type.isTop()))
                {
                    quadMap.get(quad.getFace()).add(triQuad);
                }
            }
            else if (quad.getFace() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(slopeQuad, false, type.isTop()))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slopeQuad, !type.isTop());
                    quadMap.get(null).add(slopeQuad);
                }
            }
            else if (quad.getFace() == dir.rotateY())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(slopeQuad, true, type.isTop()))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slopeQuad, !type.isTop());
                    quadMap.get(null).add(slopeQuad);
                }
            }
        }
    }
}