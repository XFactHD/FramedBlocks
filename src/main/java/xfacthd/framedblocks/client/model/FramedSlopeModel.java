package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

import java.util.List;
import java.util.Map;

public class FramedSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final SlopeType type;

    public FramedSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(PropertyHolder.FACING_HOR);
        type = state.getValue(PropertyHolder.SLOPE_TYPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type == SlopeType.HORIZONTAL)
        {
            if (quad.getDirection() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.createSideSlopeQuad(slopeQuad, true);
                quadMap.get(null).add(slopeQuad);
            }
            else if (quad.getDirection() == Direction.UP || quad.getDirection() == Direction.DOWN)
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, dir))
                {
                    quadMap.get(quad.getDirection()).add(triQuad);
                }
            }
        }
        else
        {
            if (quad.getDirection() == dir.getOpposite())
            {
                BakedQuad slopeQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.createTopBottomSlopeQuad(slopeQuad, type == SlopeType.BOTTOM);
                quadMap.get(null).add(slopeQuad);
            }
            else if (quad.getDirection() == dir.getClockWise() || quad.getDirection() == dir.getCounterClockWise())
            {
                BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideTriangleQuad(triQuad, quad.getDirection() == dir.getClockWise(), type == SlopeType.TOP))
                {
                    quadMap.get(quad.getDirection()).add(triQuad);
                }
            }
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedSlope.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.SOUTH);
    }
}