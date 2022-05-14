package xfacthd.framedblocks.client.model;

import com.google.common.base.Preconditions;
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
import xfacthd.framedblocks.common.data.Rotation;

import java.util.List;
import java.util.Map;

public class FramedSlopePanelModel extends FramedBlockModel
{
    private static final float ANGLE = (float) Math.toDegrees(Math.atan(.5));

    private final Direction facing;
    private final Rotation rotation;
    private final Direction orientation;
    private final boolean front;

    public FramedSlopePanelModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.orientation = rotation.withFacing(facing);
        this.front = state.getValue(PropertyHolder.FRONT);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        boolean yAxis = Utils.isY(orientation);
        if (face == orientation.getOpposite())
        {
            Direction cutDir = front ? facing : facing.getOpposite();
            BakedQuad slabQuad = ModelUtils.duplicateQuad(quad);
            if ((yAxis && BakedQuadTransformer.createTopBottomQuad(slabQuad, cutDir, .5F)) ||
                (!yAxis && BakedQuadTransformer.createVerticalSideQuad(slabQuad, cutDir, .5F))
            )
            {
                quadMap.get(face).add(slabQuad);
            }
        }
        else if (face == facing.getOpposite())
        {
            BakedQuad slopeQuad = createSlope(quad, facing, orientation);
            if (!front)
            {
                BakedQuadTransformer.offsetQuadInDir(slopeQuad, facing, .5F);
            }
            quadMap.get(null).add(slopeQuad);
        }
        else if (face == facing)
        {
            if (front)
            {
                BakedQuad baseQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.setQuadPosInFacingDir(baseQuad, .5F);
                quadMap.get(null).add(baseQuad);
            }
        }
        else if (face != orientation)
        {
            float depth = front ? 1F : .5F;
            float offset = front ? .5F : 0F;

            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (yAxis)
            {
                if (BakedQuadTransformer.createVerticalSideTriangleQuad(triQuad, face == facing.getClockWise(), rotation == Rotation.DOWN, depth, offset))
                {
                    if (front)
                    {
                        BakedQuadTransformer.createVerticalSideQuad(triQuad, facing, .5F);
                    }
                    quadMap.get(face).add(triQuad);
                }
            }
            else
            {
                if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, facing.getOpposite(), rotation == Rotation.RIGHT, depth, offset))
                {
                    if (front)
                    {
                        BakedQuadTransformer.createTopBottomQuad(triQuad, facing, .5F);
                    }
                    quadMap.get(face).add(triQuad);
                }
            }
        }
    }



    public static BakedQuad createSlope(BakedQuad quad, Direction facing, Direction orientation)
    {
        Preconditions.checkArgument(facing.getAxis() != orientation.getAxis(), "Directions must be perpendicular");

        Vector3f origin = new Vector3f(
                facing == Direction.WEST || (Utils.isZ(facing) && orientation == Direction.WEST) ? 1 : 0,
                orientation == Direction.DOWN ? 1 : 0,
                facing == Direction.NORTH || (Utils.isX(facing) && orientation == Direction.NORTH) ? 1 : 0
        );

        float angle = Utils.isPositive(orientation) == Utils.isY(orientation) == Utils.isX(facing) == Utils.isPositive(facing) ? -ANGLE : ANGLE;

        BakedQuad slope = ModelUtils.duplicateQuad(quad);
        BakedQuadTransformer.rotateQuadAroundAxis(
                slope,
                Utils.isY(orientation) ? facing.getClockWise().getAxis() : Direction.Axis.Y,
                origin,
                angle,
                true
        );
        return slope;
    }

    public static BlockState itemSource()
    {
        return FBContent.blockFramedSlopePanel.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
