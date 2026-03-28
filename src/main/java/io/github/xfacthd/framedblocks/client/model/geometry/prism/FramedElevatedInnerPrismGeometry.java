package io.github.xfacthd.framedblocks.client.model.geometry.prism;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedElevatedInnerPrismGeometry extends Geometry {
    private final Direction facing;
    private final Direction.Axis axis;
    private final boolean altSlope;

    public FramedElevatedInnerPrismGeometry(GeometryFactory.Context ctx) {
        DirectionAxis dirAxis = ctx.state().getValue(PropertyHolder.FACING_AXIS);
        this.facing = dirAxis.direction();
        this.axis = dirAxis.axis();
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        boolean yFacing = DirUtils.isY(facing);
        boolean yAxis = axis == Direction.Axis.Y;
        Direction quadFace = quad.direction();
        boolean quadOnFacingAxis = quadFace.getAxis() == facing.getAxis();
        boolean quadOnAxis = quadFace.getAxis() == axis;

        if (!altSlope && yFacing && !quadOnAxis && !quadOnFacingAxis) { // Slopes for Y facing without Y_SLOPE
            boolean up = facing == Direction.UP;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap, null);
        } else if (altSlope && yFacing && quadFace == facing) { // Slopes for Y facing with Y_SLOPE
            Direction onAxis = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

            Direction offAxisCW = onAxis.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(offAxisCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap, null);

            Direction offAxisCCW = onAxis.getCounterClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(offAxisCCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap, null);
        } else if (!yFacing && yAxis && !quadOnAxis && quadOnFacingAxis) { // Slopes for horizontal facing and Y axis without Y_SLOPE
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getClockWise(), .5F))
                    .apply(Modifiers.makeHorizontalSlope(true, 45))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getCounterClockWise(), .5F))
                    .apply(Modifiers.makeHorizontalSlope(false, 45))
                    .export(quadMap, null);
        } else if (!altSlope && !yFacing && !yAxis && quadFace == facing) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, .5F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, .5F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap, null);
        } else if (altSlope && !yFacing && !yAxis && DirUtils.isY(quadFace)) { // Slopes for horizontal facing and Y axis with Y_SLOPE
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap, null);
        } else if (quadOnAxis) {
            if (yAxis) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 0F, 1F))
                        .export(quadMap, quadFace);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 1F, 0F))
                        .export(quadMap, quadFace);
            } else if (yFacing) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadFace.getClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 0F, 1F))
                        .export(quadMap, quadFace);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadFace.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 1F, 0F))
                        .export(quadMap, quadFace);
            } else { //!yAxis && !yFacing
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, .5F))
                        .apply(Modifiers.cut(facing, 1F, 0F))
                        .export(quadMap, quadFace);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, .5F))
                        .apply(Modifiers.cut(facing, 0F, 1F))
                        .export(quadMap, quadFace);
            }
        }
    }

    @Override
    public boolean transformAllQuads() {
        return altSlope || DirUtils.isY(facing) || axis == Direction.Axis.Y;
    }
}
