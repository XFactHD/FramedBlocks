package xfacthd.framedblocks.client.model;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

import java.util.List;
import java.util.Map;

public class FramedSlopePanelModel extends FramedBlockModel
{
    private static final float SLOPE_ANGLE = (float) Math.toDegrees(Math.atan(.5));

    private final Direction facing;
    private final HorizontalRotation rotation;
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
            if (Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(cutDir, .5F))
                        .export(quadMap.get(face));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                        .export(quadMap.get(face));
            }
        }
        else if (face == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(createSlope(facing, orientation))
                    .applyIf(Modifiers.offset(facing, .5F), !front)
                    .export(quadMap.get(null));
        }
        else if (face == facing)
        {
            if (front)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
        else if (face != orientation)
        {
            if (yAxis)
            {
                boolean up = orientation == Direction.UP;
                float top =    up ? (front ? .5F :  0F) : (front ?  1F : .5F);
                float bottom = up ? (front ?  1F : .5F) : (front ? .5F :  0F);
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), top, bottom))
                        .applyIf(Modifiers.cutSideLeftRight(facing, .5F), front)
                        .export(quadMap.get(face));
            }
            else
            {
                boolean rightRot = rotation == HorizontalRotation.RIGHT;
                float right = rightRot ? (front ?  1F : .5F) : (front ? .5F :  0F);
                float left =  rightRot ? (front ? .5F :  0F) : (front ?  1F : .5F);
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), right, left))
                        .applyIf(Modifiers.cutTopBottom(facing, .5F), front)
                        .export(quadMap.get(face));
            }
        }
    }



    public static QuadModifier.Modifier createSlope(Direction facing, Direction orientation)
    {
        Preconditions.checkArgument(facing.getAxis() != orientation.getAxis(), "Directions must be perpendicular");

        if (Utils.isY(orientation))
        {
            return Modifiers.makeVerticalSlope(orientation == Direction.UP, SLOPE_ANGLE);
        }
        else
        {
            return Modifiers.makeHorizontalSlope(orientation == facing.getCounterClockWise(), SLOPE_ANGLE);
        }
    }

    public static BlockState itemSource()
    {
        return FBContent.blockFramedSlopePanel.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
