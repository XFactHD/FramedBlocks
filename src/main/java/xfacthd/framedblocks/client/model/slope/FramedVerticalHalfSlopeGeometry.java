package xfacthd.framedblocks.client.model.slope;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.TranslatedItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.util.Utils;

public class FramedVerticalHalfSlopeGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean ySlope;

    public FramedVerticalHalfSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();

        if (!ySlope && quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeHorizontalSlope(false, 45))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(null));
        }
        else if (ySlope && quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeHorizontalSlope(true, 45))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quadDir))
        {
            boolean needOffset = top == (quadDir == Direction.DOWN);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1, 0))
                    .applyIf(Modifiers.setPosition(.5F), needOffset)
                    .export(quadMap.get(needOffset ? null : quadDir));
        }
        else if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));
        }
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return TranslatedItemModelInfo.HAND_Y_HALF_UP;
    }
}
