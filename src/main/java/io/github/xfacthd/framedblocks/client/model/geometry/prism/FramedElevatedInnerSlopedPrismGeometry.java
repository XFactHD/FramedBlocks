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
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedElevatedInnerSlopedPrismGeometry extends Geometry
{
    private final Direction facing;
    private final Direction orientation;
    private final boolean altSlope;

    public FramedElevatedInnerSlopedPrismGeometry(GeometryFactory.Context ctx)
    {
        CompoundDirection cmpDir = ctx.state().getValue(PropertyHolder.FACING_DIR);
        this.facing = cmpDir.direction();
        this.orientation = cmpDir.orientation();
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        boolean yFacing = DirUtils.isY(facing);
        boolean yOrient = DirUtils.isY(orientation);
        Direction quadFace = quad.direction();

        if (quadFace == facing)
        {
            if (altSlope && yFacing)
            {
                boolean up = orientation == Direction.UP;

                // Tilted triangle for vertical facing with Y_SLOPE
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSmallTriangle(orientation))
                        .apply(Modifiers.makeVerticalSlope(orientation, up ? -45 : 45))
                        .export(quadMap, null);

                // Side slope for vertical facing with Y_SLOPE
                Direction oriCW = orientation.getClockWise();
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(oriCW, .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(oriCW, up ? -45 : 45))
                        .export(quadMap, null);

                // Side slope for vertical facing with Y_SLOPE
                Direction oriCCW = orientation.getCounterClockWise();
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(oriCCW, .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(oriCCW, up ? -45 : 45))
                        .export(quadMap, null);
            }
            else if (!altSlope && !yFacing && yOrient)
            {
                // Tilted triangle for horizontal facing and vertical orientation without Y_SLOPE
                boolean up = orientation == Direction.UP;
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSmallTriangle(orientation))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap, null);
            }

            if (!yFacing && !yOrient)
            {
                // Tilted triangle for horizontal facing and horizontal orientation
                boolean right = orientation == facing.getClockWise();
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(orientation, .5F))
                        .apply(Modifiers.cutSmallTriangle(orientation))
                        .apply(Modifiers.makeHorizontalSlope(right, 45))
                        .export(quadMap, null);

                if (!altSlope)
                {
                    // Side slope for horizontal facing and horizontal orientation without Y_SLOPE
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(Direction.DOWN, .5F))
                            .apply(Modifiers.cut(orientation.getOpposite(), 1, 0))
                            .apply(Modifiers.makeVerticalSlope(false, 45))
                            .export(quadMap, null);

                    // Side slope for horizontal facing and horizontal orientation without Y_SLOPE
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(Direction.UP, .5F))
                            .apply(Modifiers.cut(orientation.getOpposite(), 0, 1))
                            .apply(Modifiers.makeVerticalSlope(true, 45))
                            .export(quadMap, null);
                }
            }
            else if (!yFacing/* && yOrient*/)
            {
                // Side slope for horizontal facing and vertical orientation
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadFace.getClockWise(), .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), 0, 1))
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .export(quadMap, null);

                // Side slope for horizontal facing and vertical orientation
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadFace.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), 1, 0))
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .export(quadMap, null);
            }
        }
        else if (quadFace == orientation)
        {
            if (yOrient)
            {
                // Front face for horizontal facing and vertical orientation
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 0F, 1F))
                        .export(quadMap, quadFace);

                // Front face for horizontal facing and vertical orientation
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 1F, 0F))
                        .export(quadMap, quadFace);

                if (altSlope)
                {
                    // Tilted triangle for horizontal facing and vertical orientation with Y_SLOPE
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSmallTriangle(facing.getOpposite()))
                            .apply(Modifiers.makeVerticalSlope(facing, 45))
                            .export(quadMap, null);
                }
            }
            else if (yFacing)
            {
                // Front face for vertical facing
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadFace.getClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 0F, 1F))
                        .export(quadMap, quadFace);

                // Front face for vertical facing
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadFace.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(facing, 1F, 0F))
                        .export(quadMap, quadFace);

                if (!altSlope)
                {
                    // Tilted triangle for vertical facing without Y_SLOPE
                    boolean up = facing == Direction.UP;
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSmallTriangle(facing.getOpposite()))
                            .apply(Modifiers.makeVerticalSlope(up, 45))
                            .export(quadMap, null);
                }
            }
            else //!yOrient && !yFacing
            {
                // Front face for horizontal facing and horizontal orientation
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, .5F))
                        .apply(Modifiers.cut(facing, 1F, 0F))
                        .export(quadMap, quadFace);

                // Front face for horizontal facing and horizontal orientation
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, .5F))
                        .apply(Modifiers.cut(facing, 0F, 1F))
                        .export(quadMap, quadFace);
            }
        }
        else if (quadFace.getAxis() != orientation.getAxis() && quadFace.getAxis() != facing.getAxis())
        {
            if (altSlope && !yFacing && !yOrient)
            {
                // Side slope for horizontal facing and horizontal orientation with Y_SLOPE
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(facing, 45))
                        .export(quadMap, null);

                // Side slope for horizontal facing and horizontal orientation with Y_SLOPE
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(facing, 45))
                        .export(quadMap, null);
            }
            else if (!altSlope && yFacing)
            {
                // Side slope for vertical facing without Y_SLOPE
                boolean up = facing == Direction.UP;
                float top = up ? 1 : 0;
                float bottom = up ? 0 : 1;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .apply(Modifiers.cut(orientation.getOpposite(), top, bottom))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap, null);
            }
        }
    }

    @Override
    public boolean transformAllQuads()
    {
        if (altSlope)
        {
            return true;
        }
        return DirUtils.isY(facing) || DirUtils.isY(orientation);
    }
}
