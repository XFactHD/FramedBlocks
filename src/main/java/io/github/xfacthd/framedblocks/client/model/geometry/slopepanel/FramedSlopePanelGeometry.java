package io.github.xfacthd.framedblocks.client.model.geometry.slopepanel;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

public class FramedSlopePanelGeometry extends Geometry
{
    public static final float SLOPE_ANGLE = (float) Math.toDegrees(Math.atan(.5));
    public static final float SLOPE_ANGLE_VERT = (float) (90D - Math.toDegrees(Math.atan(.5)));

    private final Direction facing;
    private final HorizontalRotation rotation;
    private final Direction orientation;
    private final Direction.Axis triangleAxis;
    private final boolean front;
    private final boolean ySlope;

    public FramedSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.orientation = rotation.withFacing(facing);
        this.triangleAxis = rotation.rotate(Rotation.CLOCKWISE_90).withFacing(facing).getAxis();
        this.front = ctx.state().getValue(PropertyHolder.FRONT);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        boolean yAxis = DirUtils.isY(orientation);
        if (face == orientation.getOpposite())
        {
            Direction cutDir = front ? facing : facing.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap.get(face));
        }
        else if ((!rotation.isVertical() || !ySlope) && face == facing.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(createSlope(facing, orientation))
                    .applyIf(Modifiers.offset(facing, .5F), !front)
                    .export(quadMap.get(null));
        }
        else if (ySlope && isVerticalSlopeQuad(rotation, face))
        {
            QuadModifier.of(quad)
                    .apply(createVerticalSlope(facing, orientation))
                    .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                    .export(quadMap.get(null));
        }
        else if (face == facing)
        {
            if (front)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
        else if (face.getAxis() == triangleAxis)
        {
            if (yAxis)
            {
                boolean up = orientation == Direction.UP;
                float top =    up ? (front ? .5F :  0F) : (front ?  1F : .5F);
                float bottom = up ? (front ?  1F : .5F) : (front ? .5F :  0F);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), top, bottom))
                        .applyIf(Modifiers.cut(facing, .5F), front)
                        .export(quadMap.get(face));
            }
            else
            {
                boolean rightRot = rotation == HorizontalRotation.RIGHT;
                float right = rightRot ? (front ?  1F : .5F) : (front ? .5F :  0F);
                float left =  rightRot ? (front ? .5F :  0F) : (front ?  1F : .5F);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), right, left))
                        .applyIf(Modifiers.cut(facing, .5F), front)
                        .export(quadMap.get(face));
            }
        }
    }



    public static boolean isVerticalSlopeQuad(HorizontalRotation rotation, Direction face)
    {
        return switch (rotation)
                {
                    case DOWN -> face == Direction.DOWN;
                    case UP -> face == Direction.UP;
                    default -> false;
                };
    }

    public static QuadModifier.Modifier createSlope(Direction facing, Direction orientation)
    {
        Preconditions.checkArgument(facing.getAxis() != orientation.getAxis(), "Directions must be perpendicular");

        if (DirUtils.isY(orientation))
        {
            return Modifiers.makeVerticalSlope(orientation == Direction.UP, SLOPE_ANGLE);
        }
        else
        {
            return Modifiers.makeHorizontalSlope(orientation == facing.getCounterClockWise(), SLOPE_ANGLE);
        }
    }

    public static QuadModifier.Modifier createVerticalSlope(Direction facing, Direction orientation)
    {
        Preconditions.checkArgument(facing.getAxis() != orientation.getAxis(), "Directions must be perpendicular");
        return Modifiers.makeVerticalSlope(facing.getOpposite(), SLOPE_ANGLE_VERT);
    }
}
