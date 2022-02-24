package xfacthd.framedblocks.client.model;

import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedSlopeSlabModel extends FramedBlockModel
{
    private static final float ANGLE = (float) (90D - Math.toDegrees(Math.atan(.5)));
    private final Direction facing;
    private final boolean top;
    private final boolean topHalf;

    public FramedSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.topHalf = state.getValue(PropertyHolder.TOP_HALF);
    }

    public FramedSlopeSlabModel(BakedModel baseModel)
    {
        this(
                FBContent.blockFramedSlopeSlab.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH),
                baseModel
        );
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
}
