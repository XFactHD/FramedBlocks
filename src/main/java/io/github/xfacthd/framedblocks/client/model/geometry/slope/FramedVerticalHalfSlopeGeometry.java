package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
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
    private final boolean ySlope;

    public FramedVerticalHalfSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        Direction vertEdge = top ? Direction.DOWN : Direction.UP;

        if (!ySlope && quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeHorizontalSlope(false, 45))
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap.get(null));
        }
        else if (ySlope && quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeHorizontalSlope(true, 45))
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap.get(null));
        }
        else if (DirUtils.isY(quadDir))
        {
            boolean needOffset = top == (quadDir == Direction.DOWN);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 1, 0))
                    .applyIf(Modifiers.setPosition(.5F), needOffset)
                    .export(quadMap.get(needOffset ? null : quadDir));
        }
        else if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}
