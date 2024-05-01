package xfacthd.framedblocks.client.model.slab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedCenteredPanelGeometry extends Geometry
{
    private final Direction dir;

    public FramedCenteredPanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_NE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir.getAxis() == dir.getAxis())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(12F/16F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getAxis(), 12F/16F))
                    .export(quadMap.get(quadDir));
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(12F/16F))
                    .export(quadMap.get(quadDir));
        }
    }
}
