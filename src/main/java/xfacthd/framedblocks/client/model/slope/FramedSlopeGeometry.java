package xfacthd.framedblocks.client.model.slope;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedSlopeGeometry implements Geometry
{
    private final Direction dir;
    private final SlopeType type;
    private final boolean ySlope;

    public FramedSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.SLOPE_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (type == SlopeType.HORIZONTAL)
        {
            if (quad.getDirection() == dir.getOpposite())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .export(quadMap.get(null));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1, 0))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            boolean top = type == SlopeType.TOP;
            if (!ySlope && quadDir == dir.getOpposite())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
            else if (ySlope && Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .export(quadMap.get(null));
            }
            else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}