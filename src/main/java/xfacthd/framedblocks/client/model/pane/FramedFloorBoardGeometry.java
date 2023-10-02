package xfacthd.framedblocks.client.model.pane;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

public class FramedFloorBoardGeometry implements Geometry
{
    private final boolean top;

    public FramedFloorBoardGeometry(GeometryFactory.Context ctx)
    {
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if ((!top && face == Direction.UP) || (top && face == Direction.DOWN))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(1F/16F))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(quad.getDirection()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, 1F/16F))
                    .export(quadMap.get(quad.getDirection()));
        }
    }
}