package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedSlopeSlabModel extends FramedBlockModel
{
    private static final float ANGLE = (float) (90D - Math.toDegrees(Math.atan(.5)));
    private final Direction facing;
    private final boolean top;
    private final boolean topHalf;

    public FramedSlopeSlabModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(PropertyHolder.FACING_HOR);
        this.top = state.getValue(PropertyHolder.TOP);
        this.topHalf = state.getValue(PropertyHolder.TOP_HALF);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == facing.getOpposite())
        {
            BakedQuad slope = createSlope(quad, facing, top);
            if (topHalf != top)
            {
                BakedQuadTransformer.offsetQuadInDir(slope, top ? Direction.DOWN : Direction.UP, .5F);
            }
            quadMap.get(null).add(slope);
        }
        else if (face == facing)
        {
            BakedQuad slabSide = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slabSide, topHalf, .5F))
            {
                quadMap.get(face).add(slabSide);
            }
        }
        else if (face == facing.getClockWise() || face == facing.getCounterClockWise())
        {
            BakedQuad triangle = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideTriangleQuad(triangle, face == facing.getClockWise(), top, .5F, topHalf == top ? 0F : .5F) &&
                (topHalf == top || BakedQuadTransformer.createHorizontalSideQuad(triangle, !top, .5F))
            )
            {
                quadMap.get(face).add(triangle);
            }
        }
        else if ((top && !topHalf && face == Direction.UP) || (!top && topHalf && face == Direction.DOWN))
        {
            BakedQuad topBottom = ModelUtils.duplicateQuad(quad);
            BakedQuadTransformer.setQuadPosInFacingDir(topBottom, .5F);
            quadMap.get(null).add(topBottom);
        }
    }

    public static BakedQuad createSlope(BakedQuad quad, Direction facing, boolean top)
    {
        Vector3f origin = new Vector3f(
                facing == Direction.WEST ? 1 : 0,
                top ? 1 : 0,
                facing == Direction.NORTH ? 1 : 0
        );
        float angle = (Utils.isPositive(facing) == top == Utils.isZ(facing)) ? -ANGLE : ANGLE;

        BakedQuad slope = ModelUtils.duplicateQuad(quad);
        BakedQuadTransformer.rotateQuadAroundAxis(slope, facing.getClockWise().getAxis(), origin, angle, true);
        return slope;
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedSlopeSlab.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.SOUTH);
    }
}
