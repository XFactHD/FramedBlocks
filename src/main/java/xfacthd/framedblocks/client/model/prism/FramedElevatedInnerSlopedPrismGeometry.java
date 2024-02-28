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
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public class FramedElevatedInnerSlopedPrismGeometry extends Geometry
{
    private final Direction facing;
    private final Direction orientation;
    private final boolean ySlope;

    public FramedElevatedInnerSlopedPrismGeometry(GeometryFactory.Context ctx)
    {
        CompoundDirection cmpDir = ctx.state().getValue(PropertyHolder.FACING_DIR);
        this.facing = cmpDir.direction();
        this.orientation = cmpDir.orientation();
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        boolean yFacing = Utils.isY(facing);
        boolean yOrient = Utils.isY(orientation);
        Direction quadFace = quad.getDirection();

        if (quadFace == facing)
        {
            if (ySlope && yFacing)
            {
                boolean up = orientation == Direction.UP;

                // Tilted triangle for vertical facing with Y_SLOPE
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(orientation))
                        .apply(Modifiers.makeVerticalSlope(orientation, up ? -45 : 45))
                        .export(quadMap.get(null));

                // Side slope for vertical facing with Y_SLOPE
                Direction oriCW = orientation.getClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(oriCW, .5F))
                        .apply(Modifiers.cutTopBottom(orientation.getOpposite(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(oriCW, up ? -45 : 45))
                        .export(quadMap.get(null));

                // Side slope for vertical facing with Y_SLOPE
                Direction oriCCW = orientation.getCounterClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(oriCCW, .5F))
                        .apply(Modifiers.cutTopBottom(orientation.getOpposite(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(oriCCW, up ? -45 : 45))
                        .export(quadMap.get(null));
            }
            else if (!ySlope && !yFacing && yOrient)
            {
                // Tilted triangle for horizontal facing and vertical orientation without Y_SLOPE
                boolean up = orientation == Direction.UP;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(orientation))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap.get(null));
            }

            if (!yFacing && !yOrient)
            {
                // Tilted triangle for horizontal facing and horizontal orientation
                boolean right = orientation == facing.getClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(orientation, .5F))
                        .apply(Modifiers.cutSmallTriangle(orientation))
                        .apply(Modifiers.makeHorizontalSlope(right, 45))
                        .export(quadMap.get(null));

                if (!ySlope)
                {
                    // Side slope for horizontal facing and horizontal orientation without Y_SLOPE
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(true, .5F))
                            .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), 1, 0))
                            .apply(Modifiers.makeVerticalSlope(false, 45))
                            .export(quadMap.get(null));

                    // Side slope for horizontal facing and horizontal orientation without Y_SLOPE
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(false, .5F))
                            .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), 0, 1))
                            .apply(Modifiers.makeVerticalSlope(true, 45))
                            .export(quadMap.get(null));
                }
            }
            else if (!yFacing/* && yOrient*/)
            {
                boolean up = orientation == Direction.UP;

                // Side slope for horizontal facing and vertical orientation
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(true, .5F))
                        .apply(Modifiers.cutSideUpDown(up, 0, 1))
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .export(quadMap.get(null));

                // Side slope for horizontal facing and vertical orientation
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(false, .5F))
                        .apply(Modifiers.cutSideUpDown(up, 1, 0))
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (quadFace == orientation)
        {
            if (yOrient)
            {
                // Front face for horizontal facing and vertical orientation
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));

                // Front face for horizontal facing and vertical orientation
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));

                if (ySlope)
                {
                    // Tilted triangle for horizontal facing and vertical orientation with Y_SLOPE
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSmallTriangle(facing.getOpposite()))
                            .apply(Modifiers.makeVerticalSlope(facing, 45))
                            .export(quadMap.get(null));
                }
            }
            else if (yFacing)
            {
                // Front face for vertical facing
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadFace.getClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, 0F, 1F))
                        .export(quadMap.get(quadFace));

                // Front face for vertical facing
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadFace.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, 1F, 0F))
                        .export(quadMap.get(quadFace));

                if (!ySlope)
                {
                    // Tilted triangle for vertical facing without Y_SLOPE
                    boolean up = facing == Direction.UP;
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSmallTriangle(facing.getOpposite()))
                            .apply(Modifiers.makeVerticalSlope(up, 45))
                            .export(quadMap.get(null));
                }
            }
            else //!yOrient && !yFacing
            {
                // Front face for horizontal facing and horizontal orientation
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));

                // Front face for horizontal facing and horizontal orientation
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(false, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));
            }
        }
        else if (quadFace.getAxis() != orientation.getAxis() && quadFace.getAxis() != facing.getAxis())
        {
            if (ySlope && !yFacing && !yOrient)
            {
                // Side slope for horizontal facing and horizontal orientation with Y_SLOPE
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                        .apply(Modifiers.cutTopBottom(orientation.getOpposite(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(facing, 45))
                        .export(quadMap.get(null));

                // Side slope for horizontal facing and horizontal orientation with Y_SLOPE
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                        .apply(Modifiers.cutTopBottom(orientation.getOpposite(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(facing, 45))
                        .export(quadMap.get(null));
            }
            else if (!ySlope && yFacing)
            {
                // Side slope for vertical facing without Y_SLOPE
                boolean up = facing == Direction.UP;
                float top = up ? 1 : 0;
                float bottom = up ? 0 : 1;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(up, .5F))
                        .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), top, bottom))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap.get(null));
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
        return Utils.isY(facing) || Utils.isY(orientation);
    }
}
