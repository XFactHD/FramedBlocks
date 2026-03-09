package io.github.xfacthd.framedblocks.client.model.geometry.stairs;

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

public class FramedVerticalHalfStairsGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;

    public FramedVerticalHalfStairsGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        Direction vertEdge = top ? Direction.DOWN : Direction.UP;
        if (quadDir == dir.getOpposite() || quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite(), .5F))
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(quadDir == dir.getOpposite() ? dir.getCounterClockWise() : dir, .5F))
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (DirUtils.isY(quadDir))
        {
            boolean inset = (quadDir == Direction.UP) != top;

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
    }
}
