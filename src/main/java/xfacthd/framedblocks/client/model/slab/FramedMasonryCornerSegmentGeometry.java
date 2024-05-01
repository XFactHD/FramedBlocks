package xfacthd.framedblocks.client.model.slab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;

public class FramedMasonryCornerSegmentGeometry extends Geometry
{
    private final Direction dir;

    public FramedMasonryCornerSegmentGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();

        if (quadDir == Direction.UP)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == Direction.DOWN)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            boolean inDir = quadDir == dir;
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .applyIf(Modifiers.setPosition(.5F), inDir)
                    .export(quadMap.get(inDir ? null : quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            boolean inDir = quadDir == dir.getCounterClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .applyIf(Modifiers.setPosition(.5F), inDir)
                    .export(quadMap.get(inDir ? null : quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}
