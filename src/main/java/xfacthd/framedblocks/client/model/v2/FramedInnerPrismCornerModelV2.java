package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedInnerPrismCornerModelV2 extends FramedBlockModelV2
{
    private final Direction dir;
    private final boolean top;

    public FramedInnerPrismCornerModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(PropertyHolder.FACING_HOR);
        top = state.get(PropertyHolder.TOP);
    }

    public FramedInnerPrismCornerModelV2(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedInnerPrismCorner.getDefaultState().with(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if ((quad.getFace() == Direction.DOWN && top) || (quad.getFace() == Direction.UP && !top))
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir))
            {
                quadMap.get(quad.getFace()).add(triQuad);
            }
        }
        else if (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateY())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getFace() == dir.rotateY(), top))
            {
                quadMap.get(quad.getFace()).add(triQuad);
            }
        }

        if (quad.getFace() == dir.getOpposite())
        {
            BakedQuad prismQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createPrismTriangleQuad(prismQuad, top, false))
            {
                quadMap.get(null).add(prismQuad);
            }
        }
    }
}