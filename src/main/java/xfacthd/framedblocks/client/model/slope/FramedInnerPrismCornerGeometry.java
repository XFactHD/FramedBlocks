package xfacthd.framedblocks.client.model.slope;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;

public class FramedInnerPrismCornerGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean offset;
    private final boolean ySlope;

    public FramedInnerPrismCornerGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if ((quadDir == Direction.DOWN && top) || (quadDir == Direction.UP && !top))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1, 0))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir.getOpposite() || quadDir == dir.getClockWise())
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));
        }

        if (!ySlope && quadDir == dir.getOpposite())
        {
            if (offset)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(top, false))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(top, false))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutPrismTriangle(top, false))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            if (offset)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(dir.getOpposite(), false))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(dir.getOpposite(), false))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutPrismTriangle(dir.getOpposite(), false))
                        .export(quadMap.get(null));
            }
        }
    }
}