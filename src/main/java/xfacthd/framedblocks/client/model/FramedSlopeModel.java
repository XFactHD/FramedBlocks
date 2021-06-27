package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.List;
import java.util.Map;

public class FramedSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final SlopeType type;

    public FramedSlopeModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        type = state.get(PropertyHolder.SLOPE_TYPE);
    }

    public FramedSlopeModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedSlope.get().getDefaultState().with(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type == SlopeType.HORIZONTAL)
        {
            if (quad.getFace() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.createSideSlopeQuad(slopeQuad, true);
                quadMap.get(null).add(slopeQuad);
            }
            else if (quad.getFace() == Direction.UP || quad.getFace() == Direction.DOWN)
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir))
                {
                    quadMap.get(quad.getFace()).add(triQuad);
                }
            }
        }
        else
        {
            if (quad.getFace() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.createTopBottomSlopeQuad(slopeQuad, type == SlopeType.BOTTOM);
                quadMap.get(null).add(slopeQuad);
            }
            else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getFace() == dir.rotateY(), type == SlopeType.TOP))
                {
                    quadMap.get(quad.getFace()).add(triQuad);
                }
            }
        }
    }
}