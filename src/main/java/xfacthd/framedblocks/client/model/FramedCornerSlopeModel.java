package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.CornerType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedCornerSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final CornerType type;

    public FramedCornerSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(PropertyHolder.FACING_HOR);
        type = state.getValue(PropertyHolder.CORNER_TYPE);
    }

    public FramedCornerSlopeModel(BakedModel baseModel)
    {
        this(
                FBContent.blockFramedCornerSlope.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type.isHorizontal())
        {
            if ((quad.getDirection() == dir.getClockWise() && type.isRight()) || (quad.getDirection() == dir.getCounterClockWise() && !type.isRight()))
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, type.isRight(), type.isTop()))
                {
                    quadMap.get(quad.getDirection()).add(triQuad);
                }
            }
            else if ((quad.getDirection() == dir.getCounterClockWise() && type.isRight()) || (quad.getDirection() == dir.getClockWise() && !type.isRight()))
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(slopeQuad, !type.isRight(), type.isTop()))
                {
                    BakedQuadTransformer.createSideSlopeQuad(slopeQuad, type.isRight());
                    quadMap.get(null).add(slopeQuad);
                }
            }
            else if ((quad.getDirection() == Direction.UP && type.isTop()) || (quad.getDirection() == Direction.DOWN && !type.isTop()))
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, type.isRight() ? dir.getClockWise() : dir))
                {
                    quadMap.get(quad.getDirection()).add(triQuad);
                }
            }
            else if (quad.getDirection() == dir.getOpposite())
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
            if (quad.getDirection() == dir)
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, true, type.isTop()))
                {
                    quadMap.get(quad.getDirection()).add(triQuad);
                }
            }
            else if (quad.getDirection() == dir.getCounterClockWise())
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, false, type.isTop()))
                {
                    quadMap.get(quad.getDirection()).add(triQuad);
                }
            }
            else if (quad.getDirection() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(slopeQuad, false, type.isTop()))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slopeQuad, !type.isTop());
                    quadMap.get(null).add(slopeQuad);
                }
            }
            else if (quad.getDirection() == dir.getClockWise())
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