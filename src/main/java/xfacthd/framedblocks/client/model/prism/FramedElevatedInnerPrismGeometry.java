package xfacthd.framedblocks.client.model.prism;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public class FramedElevatedInnerPrismGeometry extends Geometry
{
    private final Direction facing;
    private final Direction.Axis axis;
    private final boolean ySlope;

    public FramedElevatedInnerPrismGeometry(GeometryFactory.Context ctx)
    {
        DirectionAxis dirAxis = ctx.state().getValue(PropertyHolder.FACING_AXIS);
        this.facing = dirAxis.direction();
        this.axis = dirAxis.axis();
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        boolean yFacing = Utils.isY(facing);
        boolean yAxis = axis == Direction.Axis.Y;
        Direction quadFace = quad.getDirection();
        boolean quadOnFacingAxis = quadFace.getAxis() == facing.getAxis();
        boolean quadOnAxis = quadFace.getAxis() == axis;

        if (!ySlope && yFacing && !quadOnAxis && !quadOnFacingAxis) // Slopes for Y facing without Y_SLOPE
        {
            boolean up = facing == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(up, .5F))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && yFacing && quadFace == facing) // Slopes for Y facing with Y_SLOPE
        {
            Direction onAxis = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

            Direction offAxisCW = onAxis.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap.get(null));

            Direction offAxisCCW = onAxis.getCounterClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap.get(null));
        }
        else if (!yFacing && yAxis && !quadOnAxis && quadOnFacingAxis) // Slopes for horizontal facing and Y axis without Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), .5F))
                    .apply(Modifiers.makeHorizontalSlope(true, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), .5F))
                    .apply(Modifiers.makeHorizontalSlope(false, 45))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && !yFacing && !yAxis && quadFace == facing)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && !yFacing && !yAxis && Utils.isY(quadFace)) // Slopes for horizontal facing and Y axis with Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
        else if (quadOnAxis)
        {
            if (yAxis)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));
            }
            else if (yFacing)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadFace.getClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, 0F, 1F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadFace.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, 1F, 0F))
                        .export(quadMap.get(quadFace));
            }
            else //!yAxis && !yFacing
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(false, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));
            }
        }
    }

    @Override
    public boolean transformAllQuads()
    {
        if (ySlope)
        {
            return true;
        }
        return Utils.isY(facing) || axis == Direction.Axis.Y;
    }
}
