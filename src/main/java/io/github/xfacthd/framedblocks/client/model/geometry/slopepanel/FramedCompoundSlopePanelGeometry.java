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

public class FramedCompoundSlopePanelGeometry extends Geometry
{
    private final Direction dir;
    private final HorizontalRotation rot;
    private final Direction orientation;
    private final Direction.Axis triangleAxis;
    private final boolean altSlope;

    public FramedCompoundSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.orientation = rot.withFacing(dir);
        this.triangleAxis = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir).getAxis();
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == orientation)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .export(quadMap, quadDir);

            if (altSlope && DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(dir.getOpposite(), .5F))
                        .export(quadMap, null);
            }
        }
        else if (quadDir == orientation.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .export(quadMap, quadDir);

            if (altSlope && DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(dir, FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap, null);
            }
        }
        else if (quadDir == dir)
        {
            if (!DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeHorizontalSlope(rot == HorizontalRotation.LEFT, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap, null);
            }
            else if (!altSlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(rot == HorizontalRotation.DOWN, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap, null);
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            if (!DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeHorizontalSlope(rot == HorizontalRotation.LEFT, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap, null);
            }
            else if (!altSlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(rot == HorizontalRotation.UP, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap, null);
            }
        }
        else if (triangleAxis == Direction.Axis.Y && DirUtils.isY(quadDir))
        {
            boolean right = rot == HorizontalRotation.RIGHT;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, right ? 1F : .5F, right ? .5F : 1F))
                    .apply(Modifiers.cut(dir.getOpposite(), right ? 1F : .5F, right ? .5F : 1F))
                    .export(quadMap, quadDir);
        }
        else if (triangleAxis != Direction.Axis.Y && quadDir.getAxis() == triangleAxis)
        {
            boolean up = rot == HorizontalRotation.UP;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, up ? 1F : .5F, up ? .5F : 1F))
                    .apply(Modifiers.cut(dir.getOpposite(), up ? .5F : 1F, up ? 1F : .5F))
                    .export(quadMap, quadDir);
        }
    }
}
