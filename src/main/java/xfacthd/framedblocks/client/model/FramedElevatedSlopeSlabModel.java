package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedElevatedSlopeSlabModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean top;

    public FramedElevatedSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
    }

    public FramedElevatedSlopeSlabModel(BakedModel baseModel)
    {
        this(
                FBContent.blockFramedElevatedSlopeSlab.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH),
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
