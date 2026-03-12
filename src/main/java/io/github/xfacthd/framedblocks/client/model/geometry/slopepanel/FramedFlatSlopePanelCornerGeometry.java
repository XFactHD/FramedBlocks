package io.github.xfacthd.framedblocks.client.model.geometry.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
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

public class FramedFlatSlopePanelCornerGeometry extends Geometry
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;
    private final boolean front;
    private final boolean altSlope;

    public FramedFlatSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
        this.front = ctx.state().getValue(PropertyHolder.FRONT);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        if (face == orientation.getOpposite())
        {
            createSideTriangle(quadMap, quad, facing, rotRotation, front, false);
        }
        else if (face == rotOrientation.getOpposite())
        {
            createSideTriangle(quadMap, quad, facing, rotation, front, false);
        }
        else if (face == facing.getOpposite())
        {
            if (!altSlope || !DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(createSlopeTriangle(facing, orientation, false, face))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, orientation))
                        .applyIf(Modifiers.offset(facing, .5F), !front)
                        .export(quadMap, null);
            }

            if (!altSlope || !DirUtils.isY(rotOrientation))
            {
                QuadModifier.of(quad)
                        .apply(createSlopeTriangle(facing, rotOrientation, true, face))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, rotOrientation))
                        .applyIf(Modifiers.offset(facing, .5F), !front)
                        .export(quadMap, null);
            }
        }
        else if (altSlope && DirUtils.isY(orientation) && face == orientation)
        {
            QuadModifier.of(quad)
                    .apply(createVerticalSlopeTriangle(facing, orientation, false))
                    .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, orientation))
                    .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                    .export(quadMap, null);
        }
        else if (altSlope && DirUtils.isY(rotOrientation) && face == rotOrientation)
        {
            QuadModifier.of(quad)
                    .apply(createVerticalSlopeTriangle(facing, rotOrientation, true))
                    .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, rotOrientation))
                    .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                    .export(quadMap, null);
        }
        else if (face == facing && front)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
    }

    public static QuadModifier.Modifier createSlopeTriangle(Direction facing, Direction orientation, boolean second, Direction quadDir)
    {
        if (DirUtils.isY(orientation))
        {
            boolean down = orientation == Direction.UP;
            Direction cutDir = down ? Direction.DOWN : Direction.UP;
            float right = (down != second) ? 0 : 1;
            float left  = (down != second) ? 1 : 0;
            return Modifiers.cut(cutDir, right, left);
        }
        else
        {
            boolean right = orientation == facing.getClockWise();
            Direction cutDir = right ? quadDir.getClockWise() : quadDir.getCounterClockWise();
            float top = (right != second) ? 0 : 1;
            float bot = (right != second) ? 1 : 0;
            return Modifiers.cut(cutDir, top, bot);
        }
    }

    public static QuadModifier.Modifier createVerticalSlopeTriangle(
            Direction facing, Direction orientation, boolean second
    )
    {
        boolean down = orientation == Direction.DOWN;
        float right = (second == down) ? 0 : 1;
        float left  = (second == down) ? 1 : 0;
        return Modifiers.cut(facing.getOpposite(), right, left);
    }

    public static void createSideTriangle(
            QuadMapBuilder quadMap,
            BakedQuad quad,
            Direction facing,
            HorizontalRotation rotation,
            boolean front,
            boolean extended
    )
    {
        Direction face = quad.direction();
        Direction orientation = rotation.withFacing(facing);
        boolean yAxis = DirUtils.isY(orientation);

        if (yAxis)
        {
            boolean up = orientation == Direction.UP;
            float top =    up ? (front ? .5F :  0F) : (front ?  1F : .5F);
            float bottom = up ? (front ?  1F : .5F) : (front ? .5F :  0F);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), top, bottom))
                    .applyIf(Modifiers.cut(facing, .5F), front && !extended)
                    .export(quadMap, face);
        }
        else
        {
            boolean rightRot = rotation == HorizontalRotation.RIGHT;
            float right = rightRot ? (front ?  1F : .5F) : (front ? .5F :  0F);
            float left =  rightRot ? (front ? .5F :  0F) : (front ?  1F : .5F);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), right, left))
                    .applyIf(Modifiers.cut(facing, .5F), front && !extended)
                    .export(quadMap, face);
        }
    }
}
