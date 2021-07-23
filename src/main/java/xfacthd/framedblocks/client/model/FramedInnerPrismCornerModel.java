package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedInnerPrismCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final boolean offset;

    public FramedInnerPrismCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(PropertyHolder.FACING_HOR);
        top = state.getValue(PropertyHolder.TOP);
        offset = state.getValue(PropertyHolder.OFFSET);
    }

    public FramedInnerPrismCornerModel(BakedModel baseModel)
    {
        this(
                FBContent.blockFramedInnerPrismCorner.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if ((quad.getDirection() == Direction.DOWN && top) || (quad.getDirection() == Direction.UP && !top))
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir))
            {
                quadMap.get(quad.getDirection()).add(triQuad);
            }
        }
        else if (quad.getDirection() == dir.getOpposite() || quad.getDirection() == dir.getClockWise())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getDirection() == dir.getClockWise(), top))
            {
                quadMap.get(quad.getDirection()).add(triQuad);
            }
        }

        if (quad.getDirection() == dir.getOpposite())
        {
            BakedQuad prismQuad = ModelUtils.duplicateQuad(quad);
            if (!offset)
            {
                if (BakedQuadTransformer.createPrismTriangleQuad(prismQuad, top, false))
                {
                    quadMap.get(null).add(prismQuad);
                }
            }
            else
            {
                if (BakedQuadTransformer.createVerticalSideQuad(prismQuad, dir.getClockWise(), .5F))
                {
                    BakedQuadTransformer.offsetQuadInDir(prismQuad, dir.getClockWise(), .5F);
                    if (BakedQuadTransformer.createPrismTriangleQuad(prismQuad, top, false))
                    {
                        quadMap.get(null).add(prismQuad);
                    }
                }

                prismQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createVerticalSideQuad(prismQuad, dir.getCounterClockWise(), .5F))
                {
                    BakedQuadTransformer.offsetQuadInDir(prismQuad, dir.getCounterClockWise(), .5F);
                    if (BakedQuadTransformer.createPrismTriangleQuad(prismQuad, top, false))
                    {
                        quadMap.get(null).add(prismQuad);
                    }
                }
            }
        }
    }
}