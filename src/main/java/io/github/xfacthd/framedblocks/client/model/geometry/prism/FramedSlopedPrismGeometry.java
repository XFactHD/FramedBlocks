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
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedSlopedPrismGeometry extends Geometry
{
    private final Direction facing;
    private final Direction orientation;
    private final boolean altSlope;

    public FramedSlopedPrismGeometry(GeometryFactory.Context ctx)
    {
        CompoundDirection cmpDir = ctx.state().getValue(PropertyHolder.FACING_DIR);
        this.facing = cmpDir.direction();
        this.orientation = cmpDir.orientation();
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        boolean yFacing = DirUtils.isY(facing);
        boolean yOrient = DirUtils.isY(orientation);
        Direction orientOpp = orientation.getOpposite();
        Direction quadFace = quad.direction();

        if (quadFace == orientOpp && !yOrient)
        {
            if (!yFacing) // Triangle for horizontal facing and horizontal orientation
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSmallTriangle(facing))
                        .apply(Modifiers.makeHorizontalSlope(orientation == facing.getClockWise(), 45))
                        .export(quadMap.get(null));
            }
            else if (!altSlope)  // Triangle for horizontal facing and vertical orientation without Y_SLOPE
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSmallTriangle(facing))
                        .apply(Modifiers.makeVerticalSlope(facing == Direction.UP, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (altSlope && yFacing && DirUtils.isY(quadFace)) // Triangle and slopes for vertical facing with Y_SLOPE
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeVerticalSlope(orientOpp, 45))
                    .export(quadMap.get(null));

            Direction offAxisCW = orientation.getClockWise();
            Direction offAxisCCW = orientation.getCounterClockWise();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(offAxisCW, .5F))
                    .apply(Modifiers.cut(orientOpp, 1, 0))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(offAxisCCW, .5F))
                    .apply(Modifiers.cut(orientOpp, 0, 1))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap.get(null));
        }
        else if (!altSlope && yOrient && quadFace == facing) // Tilted triangle for horizontal facing and vertical orientation without Y_SLOPE
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeVerticalSlope(orientation == Direction.DOWN, 45))
                    .export(quadMap.get(null));
        }
        else if (altSlope && yOrient && quadFace == orientOpp) // Tilted triangle for horizontal facing and vertical orientation with Y_SLOPE
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
        else if (quadFace == orientation) // Triangle
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .export(quadMap.get(quadFace));
        }
        else if (!altSlope && yFacing && quadFace.getAxis() == orientation.getClockWise().getAxis()) // Slopes for Y facing without Y_SLOPE
        {
            boolean up = facing == Direction.UP;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.cut(orientation.getOpposite(), up ? 0 : 1, up ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (yOrient && quadFace.getAxis() == facing.getClockWise().getAxis()) // Slopes for horizontal facing and vertical orientation
        {
            boolean right = quadFace == facing.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.cut(orientation.getOpposite(), right ? 1 : 0, right ? 0 : 1))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!altSlope && !yOrient && !yFacing && quadFace == facing) // Slopes for horizontal facing and horizontal orientation without Y_SLOPE
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, .5F))
                    .apply(Modifiers.cut(orientation.getOpposite(), 0, 1))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, .5F))
                    .apply(Modifiers.cut(orientation.getOpposite(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));
        }
        else if (altSlope && !yOrient && !yFacing && DirUtils.isY(quadFace)) // Slopes for horizontal facing and horizontal orientation with Y_SLOPE
        {
            boolean right = orientation == facing.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.cut(orientOpp, right ? 0 : 1, right ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
    }
}
