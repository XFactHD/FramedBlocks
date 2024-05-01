package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;

public class FramedThreewayCornerPillarGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;

    public FramedThreewayCornerPillarGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir.getCounterClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(!top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutSideUpDown(!top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }
}
