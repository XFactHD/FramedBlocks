package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedSlopeGeometry extends Geometry
{
    private final Direction dir;
    private final SlopeType type;
    private final boolean altSlope;

    public FramedSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.SLOPE_TYPE);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (type == SlopeType.HORIZONTAL)
        {
            if (!altSlope && quad.direction() == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .export(quadMap, null);
            }
            else if (altSlope && quadDir == dir.getClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .export(quadMap, null);
            }
            else if (DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), 1, 0))
                        .export(quadMap, quadDir);
            }
        }
        else
        {
            boolean top = type == SlopeType.TOP;
            if (!altSlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap, null);
            }
            else if (altSlope && DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .export(quadMap, null);
            }
            else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                        .export(quadMap, quadDir);
            }
        }
    }
}
