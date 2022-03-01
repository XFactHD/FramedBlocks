package xfacthd.framedblocks.client.model;

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

public class FramedElevatedSlopeSlabModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean top;

    public FramedElevatedSlopeSlabModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(PropertyHolder.FACING_HOR);
        this.top = state.getValue(PropertyHolder.TOP);
    }

    public FramedElevatedSlopeSlabModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedElevatedSlopeSlab.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.SOUTH),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == facing.getOpposite())
        {
            BakedQuad slope = FramedSlopeSlabModel.createSlope(quad, facing, top);
            BakedQuadTransformer.offsetQuadInDir(slope, top ? Direction.DOWN : Direction.UP, .5F);
            quadMap.get(null).add(slope);

            BakedQuad slab = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slab, top, .5F))
            {
                quadMap.get(face).add(slab);
            }
        }
        else if (face == facing.getClockWise() || face == facing.getCounterClockWise())
        {
            BakedQuad triangle = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triangle, face == facing.getClockWise(), top, .5F, .5F))
            {
                quadMap.get(face).add(triangle);
            }
        }
    }
}
