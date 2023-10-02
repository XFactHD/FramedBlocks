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

public class FramedSlabCornerGeometry implements Geometry
{
    private final Direction dir;
    private final boolean top;

    public FramedSlabCornerGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            boolean inset = (!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
        else
        {
            Direction cutDir = quadDir.getAxis() == dir.getAxis() ? dir.getClockWise() : dir.getOpposite();
            boolean inset = quadDir == dir.getOpposite() || quadDir == dir.getClockWise();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
    }
}