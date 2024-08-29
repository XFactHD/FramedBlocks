package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;

public class FramedCompoundSlopeSlabGeometry extends Geometry
{
    private final Direction dir;
    private final boolean ySlope;

    public FramedCompoundSlopeSlabGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(false, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(true, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && Utils.isY(quadDir))
        {
            Direction edge = quadDir == Direction.UP ? dir.getOpposite() : dir;
            QuadModifier.of(quad)
                    .apply(Modifiers.makeVerticalSlope(edge, FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            boolean cw = quadDir == dir.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(false, cw ? .5F : 1F, cw ? 1F : .5F))
                    .apply(Modifiers.cutSideUpDown(true, cw ? 1F : .5F, cw ? .5F : 1F))
                    .export(quadMap.get(quadDir));
        }
    }
}
