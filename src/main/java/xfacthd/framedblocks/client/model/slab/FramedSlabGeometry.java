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

public class FramedSlabGeometry extends Geometry
{
    private final boolean top;

    public FramedSlabGeometry(GeometryFactory.Context ctx)
    {
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMap quadMap, final BakedQuad quad)
    {
        if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(quad.getDirection()))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quad.getDirection()));
        }
    }
}