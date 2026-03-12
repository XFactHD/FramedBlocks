package io.github.xfacthd.framedblocks.client.model.geometry.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedThreewayCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean right;
    private final boolean altType;
    private final boolean altSlope;

    public FramedThreewayCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        Direction dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.right = ctx.state().getValue(PropertyHolder.RIGHT);
        this.dir = right ? dir.getClockWise() : dir;
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        Direction yBackFace = top ? Direction.UP : Direction.DOWN;
        if (altType)
        {
            if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace, .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace, .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace, .5F))
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), .5F, 1.5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getOpposite())
            {
                if (!right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .75F))
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), top ? 1F : 0F, top ? 0F : 1F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }

                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), .75F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cut(Direction.UP, top ? 1F : 1.5F, top ? 0F : .5F))
                            .apply(Modifiers.cut(Direction.DOWN, top ? 1.5F : 1F, top ? .5F : 0F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }
            }
            else if (quadDir == dir.getClockWise())
            {
                if (right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .75F))
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.cut(dir, top ? 1F : 0F, top ? 0F : 1F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap, null);
                }

                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), .75F))
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(Direction.UP, top ? 0F : .5F, top ? 1F : 1.5F))
                            .apply(Modifiers.cut(Direction.DOWN, top ? .5F : 0F, top ? 1.5F : 1F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap, null);
                }
            }
            else if (altSlope && quadDir == yBackFace.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .75F))
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), 1F, 0F))
                        .apply(Modifiers.cut(dir.getClockWise(), .5F, 1.5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .75F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cut(dir, 0F, 1F))
                        .apply(Modifiers.cut(dir.getOpposite(), 1.5F, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                        .export(quadMap, null);
            }
        }
        else
        {
            if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), -.5F, .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getOpposite())
            {
                if (!right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .25F))
                            .apply(Modifiers.cut(dir.getClockWise(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), top ? 1F : 0F, top ? 0F : 1F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap, null);
                }

                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), .25F))
                            .apply(Modifiers.cut(Direction.UP, top ? 1F : .5F, top ? 0F : -.5F))
                            .apply(Modifiers.cut(Direction.DOWN, top ? .5F : 1F, top ? -.5F : 0F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap, null);
                }
            }
            else if (quadDir == dir.getClockWise())
            {
                if (right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .25F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .apply(Modifiers.cut(dir, top ? 1F : 0F, top ? 0F : 1F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap, null);
                }

                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), .25F))
                            .apply(Modifiers.cut(Direction.UP, top ? 0F : -.5F, top ? 1F : .5F))
                            .apply(Modifiers.cut(Direction.DOWN, top ? -.5F : 0F, top ? .5F : 1F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap, null);
                }
            }
            else if (altSlope && quadDir == yBackFace.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .25F))
                        .apply(Modifiers.cut(dir.getClockWise(), -.5F, .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), 1F, 0F))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(yBackFace, .5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .25F))
                        .apply(Modifiers.cut(dir, 0F, 1F))
                        .apply(Modifiers.cut(dir.getOpposite(), .5F, -.5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(yBackFace, .5F))
                        .export(quadMap, null);
            }
        }
    }
}
