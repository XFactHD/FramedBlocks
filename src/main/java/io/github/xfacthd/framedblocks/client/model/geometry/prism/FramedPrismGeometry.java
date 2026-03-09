package io.github.xfacthd.framedblocks.client.model.geometry.prism;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedPrismGeometry extends Geometry
{
    private final Direction facing;
    private final Direction.Axis axis;
    private final boolean ySlope;

    public FramedPrismGeometry(GeometryFactory.Context ctx)
    {
        DirectionAxis dirAxis = ctx.state().getValue(PropertyHolder.FACING_AXIS);
        this.facing = dirAxis.direction();
        this.axis = dirAxis.axis();
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        boolean yFacing = DirUtils.isY(facing);
        boolean yAxis = axis == Direction.Axis.Y;
        Direction quadFace = quad.direction();
        boolean quadOnAxis = quadFace.getAxis() == axis;
        boolean quadOnFacingAxis = quadFace.getAxis() == facing.getAxis();

        if (!ySlope && yFacing && !quadOnAxis && !quadOnFacingAxis) // Slopes for Y facing without Y_SLOPE
        {
            boolean up = facing == Direction.UP;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && yFacing && DirUtils.isY(quadFace)) // Slopes for Y facing with Y_SLOPE
        {
            Direction onAxis = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction offAxisCW = onAxis.getClockWise();
            Direction offAxisCCW = onAxis.getCounterClockWise();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(offAxisCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(offAxisCCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap.get(null));
        }
        else if (!yFacing && yAxis && !quadOnAxis && !quadOnFacingAxis) // Slopes for horizontal facing and vertical axis
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && !yFacing && !yAxis && quadFace == facing) // Slopes for horizontal facing and horizontal axis without Y_SLOPE
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, .5F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, .5F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && !yFacing && !yAxis && DirUtils.isY(quadFace)) // Slopes for horizontal facing and horizontal axis with Y_SLOPE
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
        else if (quadFace.getAxis() == axis) // Triangles
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .export(quadMap.get(quadFace));
        }
    }
}
