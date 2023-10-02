package xfacthd.framedblocks.client.model.slab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

public class FramedPanelGeometry implements Geometry
{
    private final Direction dir;

    public FramedPanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quad.getDirection()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}