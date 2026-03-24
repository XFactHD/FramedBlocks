package io.github.xfacthd.framedblocks.client.model.geometry.slopeedge;

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

public class FramedSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final SlopeType type;
    private final boolean altType;
    private final boolean altSlope;

    public FramedSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.SLOPE_TYPE);
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
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
                            .export(quadMap, null);
                }
                else if (quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (!altSlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }
                else if (altSlope && quadDir == dir.getClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap, null);
                }
                else if (DirUtils.isY(quadDir))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), 1.5F, .5F))
                            .apply(Modifiers.cut(dir, .5F))
                            .export(quadMap, quadDir);
                }
            }
            else
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (!altSlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }
                else if (altSlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                            .export(quadMap, null);
                }
                else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.cut(dir, .5F))
                            .export(quadMap, quadDir);
                }
            }
            return;
        }

        if (type == SlopeType.HORIZONTAL)
        {
            if (!altSlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap, null);
            }
            else if (altSlope && quadDir == dir.getClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .export(quadMap, null);
            }
            else if (DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), .5F, -.5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
        }
        else
        {
            if (!altSlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap, null);
            }
            else if (altSlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F))
                        .export(quadMap, null);
            }
            else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                        .export(quadMap, quadDir);
            }
            else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
        }
    }
}
