package io.github.xfacthd.framedblocks.client.model.geometry.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedSlopeEdgeSlabGeometry extends Geometry
{
    private final Direction dir;
    private final boolean topHalf;
    private final boolean top;
    private final boolean ySlope;
    private final boolean backfaceAligned;

    public FramedSlopeEdgeSlabGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.topHalf = ctx.state().getValue(PropertyHolder.TOP_HALF);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
        this.backfaceAligned = top == topHalf;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        Direction backFace = top ? Direction.UP : Direction.DOWN;
        if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(topHalf ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap.get(dir));
        }
        else if (quadDir == backFace)
        {
            if (!backfaceAligned)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == backFace.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), backfaceAligned)
                    .export(quadMap.get(backfaceAligned ? null : quadDir));

            if (ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir, -45))
                        .apply(Modifiers.offset(backFace, backfaceAligned ? 1F : .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            if (!ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(backFace.getOpposite(), .5F))
                        .apply(Modifiers.makeVerticalSlope(top, -45))
                        .apply(Modifiers.offset(dir, 1F))
                        .applyIf(Modifiers.offset(backFace.getOpposite(), .5F), !backfaceAligned)
                        .export(quadMap.get(null));
            }
        }
        else
        {
            float lenTop = (top ? 1F : 0F) + (backfaceAligned ? 0F : .5F);
            float lenBot = (top ? 0F : 1F) + (backfaceAligned ? 0F : .5F);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(topHalf ? Direction.DOWN : Direction.UP, .5F))
                    .apply(Modifiers.cut(dir.getOpposite(), lenTop, lenBot))
                    .export(quadMap.get(quadDir));
        }
    }
}
