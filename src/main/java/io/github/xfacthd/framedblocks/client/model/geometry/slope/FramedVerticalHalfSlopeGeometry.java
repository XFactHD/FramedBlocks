package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedVerticalHalfSlopeGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean altSlope;

    public FramedVerticalHalfSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        Direction vertEdge = top ? Direction.DOWN : Direction.UP;

        if (!altSlope && quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeHorizontalSlope(false, 45))
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap, null);
        }
        else if (altSlope && quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeHorizontalSlope(true, 45))
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap, null);
        }
        else if (DirUtils.isY(quadDir))
        {
            boolean needOffset = top == (quadDir == Direction.DOWN);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 1, 0))
                    .applyIf(Modifiers.setPosition(.5F), needOffset)
                    .export(quadMap, needOffset ? null : quadDir);
        }
        else if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap, quadDir);
        }
    }
}
