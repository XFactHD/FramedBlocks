package io.github.xfacthd.framedblocks.client.model.geometry.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final SlopeType type;
    private final boolean altType;
    private final boolean ySlope;

    public FramedSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.SLOPE_TYPE);
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        boolean top = type == SlopeType.TOP;
        if (altType)
        {
            if (type == SlopeType.HORIZONTAL)
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == dir.getClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }
                else if (Utils.isY(quadDir))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), 1.5F, .5F))
                            .apply(Modifiers.cut(dir, .5F))
                            .export(quadMap.get(quadDir));
                }
            }
            else
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                            .export(quadMap.get(null));
                }
                else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.cut(dir, .5F))
                            .export(quadMap.get(quadDir));
                }
            }
            return;
        }

        if (type == SlopeType.HORIZONTAL)
        {
            if (!ySlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));
            }
            else if (ySlope && quadDir == dir.getClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(null));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), .5F, -.5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            if (!ySlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));
            }
            else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
            else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                        .export(quadMap.get(quadDir));
            }
            else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}
