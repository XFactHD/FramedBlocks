package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;

public class FramedCornerPillarGeometry implements Geometry
{
    private final Direction dir;

    public FramedCornerPillarGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir || quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), quadDir != dir)
                    .export(quadMap.get(quadDir == dir ? quadDir : null));
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            boolean isCCW = quadDir == dir.getCounterClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), !isCCW)
                    .export(quadMap.get(isCCW ? quadDir : null));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}