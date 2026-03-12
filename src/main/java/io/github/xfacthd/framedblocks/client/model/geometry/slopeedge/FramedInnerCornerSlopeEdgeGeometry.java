package io.github.xfacthd.framedblocks.client.model.geometry.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedInnerCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final CornerType type;
    private final boolean altType;
    private final boolean altSlope;

    public FramedInnerCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.CORNER_TYPE);
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        boolean top = type.isTop();
        if (type.isHorizontal())
        {
            boolean right = type.isRight();
            Direction xBackFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            Direction yBackFace = top ? Direction.UP : Direction.DOWN;
            if (altType)
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(xBackFace, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (quadDir == yBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(xBackFace, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (quadDir == xBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (quadDir == yBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(xBackFace, .5F))
                            .apply(Modifiers.cut(xBackFace.getOpposite(), right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .export(quadMap, quadDir);

                    if (altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(dir, .5F))
                                .apply(Modifiers.cut(xBackFace, .5F))
                                .apply(Modifiers.cut(xBackFace, right ? .5F : -.5F, right ? -.5F : .5F))
                                .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                                .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                                .export(quadMap, null);
                    }
                }
                else if (quadDir == xBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(yBackFace.getOpposite(), right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .export(quadMap, quadDir);

                    if (altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(dir, .5F))
                                .apply(Modifiers.cut(yBackFace, .5F))
                                .apply(Modifiers.cut(yBackFace, right ? -.5F : .5F, right ? .5F : -.5F))
                                .apply(Modifiers.makeHorizontalSlope(!right, 45))
                                .apply(Modifiers.offset(xBackFace.getOpposite(), .5F))
                                .export(quadMap, null);
                    }
                }
                else if (!altSlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(xBackFace, .5F))
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(yBackFace.getOpposite(), right ? 1F : 0F, right ? 0F : 1F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(xBackFace, .5F))
                            .apply(Modifiers.cut(xBackFace.getOpposite(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(right, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }
            }
            else
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                            .export(quadMap, quadDir);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(xBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .export(quadMap, quadDir);
                }
                else if (quadDir == yBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .export(quadMap, quadDir);
                }
                else if (quadDir == xBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .export(quadMap, quadDir);
                }
                else if (quadDir == yBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .apply(Modifiers.cut(xBackFace.getOpposite(), right ? .5F : -.5F, right ? -.5F : .5F))
                            .export(quadMap, quadDir);

                    if (altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(dir.getOpposite(), .5F))
                                .apply(Modifiers.cut(xBackFace, right ? 1.5F : .5F, right ? .5F : 1.5F))
                                .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                                .apply(Modifiers.offset(yBackFace, .5F))
                                .export(quadMap, null);
                    }
                }
                else if (quadDir == xBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .apply(Modifiers.cut(yBackFace.getOpposite(), right ? .5F : -.5F, right ? -.5F : .5F))
                            .export(quadMap, quadDir);

                    if (altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(dir.getOpposite(), .5F))
                                .apply(Modifiers.cut(yBackFace, right ? .5F : 1.5F, right ? 1.5F : .5F))
                                .apply(Modifiers.makeHorizontalSlope(!right, 45))
                                .apply(Modifiers.offset(xBackFace, .5F))
                                .export(quadMap, null);
                    }
                }
                else if (!altSlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(xBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(yBackFace, right ? 0F : 1F, right ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(right, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(xBackFace, top ? 1F : 0F, top ? 0F : 1F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap, null);
                }
            }
        }
        else
        {
            Direction bottomFace = top ? Direction.UP : Direction.DOWN;
            if (altType)
            {
                if (quadDir == dir || quadDir == dir.getCounterClockWise())
                {
                    Direction cutDir = quadDir == dir ? dir.getCounterClockWise() : dir;
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(bottomFace, .5F))
                            .apply(Modifiers.cut(cutDir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (quadDir == bottomFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap, null);
                }
                else if (altSlope && quadDir == bottomFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cut(dir, 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(bottomFace.getOpposite(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), 1F, 0F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(bottomFace.getOpposite(), .5F))
                            .export(quadMap, null);
                }
                else if (quadDir == dir.getOpposite())
                {
                    if (!altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(bottomFace, .5F))
                                .apply(Modifiers.cut(dir.getCounterClockWise(), top ? -.5F : .5F, top ? .5F : -.5F))
                                .apply(Modifiers.makeVerticalSlope(!top, 45))
                                .apply(Modifiers.offset(dir.getOpposite(), .5F))
                                .export(quadMap, null);
                    }

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(bottomFace, .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .export(quadMap, quadDir);
                }
                else if (quadDir == dir.getClockWise())
                {
                    if (!altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(bottomFace, .5F))
                                .apply(Modifiers.cut(dir, top ? -.5F : .5F, top ? .5F : -.5F))
                                .apply(Modifiers.makeVerticalSlope(!top, 45))
                                .apply(Modifiers.offset(dir.getClockWise(), .5F))
                                .export(quadMap, null);
                    }

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(bottomFace, .5F))
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .export(quadMap, quadDir);
                }
            }
            else
            {
                if (quadDir == dir || quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(bottomFace.getOpposite(), .5F))
                            .export(quadMap, quadDir);
                }
                else if (quadDir == bottomFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .export(quadMap, quadDir);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), .5F))
                            .export(quadMap, quadDir);
                }
                else if (altSlope && quadDir == bottomFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), 1F, 0F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(bottomFace, .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getClockWise(), .5F))
                            .apply(Modifiers.cut(dir, 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(bottomFace, .5F))
                            .export(quadMap, null);
                }
                else if (quadDir == dir.getOpposite())
                {
                    if (!altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(bottomFace.getOpposite(), .5F))
                                .apply(Modifiers.cut(dir.getCounterClockWise(), top ? .5F : 1.5F, top ? 1.5F : .5F))
                                .apply(Modifiers.makeVerticalSlope(!top, 45))
                                .apply(Modifiers.offset(dir, .5F))
                                .export(quadMap, null);
                    }

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(bottomFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .export(quadMap, quadDir);
                }
                else if (quadDir == dir.getClockWise())
                {
                    if (!altSlope)
                    {
                        QuadModifier.of(quad)
                                .apply(Modifiers.cut(bottomFace.getOpposite(), .5F))
                                .apply(Modifiers.cut(dir, top ? .5F : 1.5F, top ? 1.5F : .5F))
                                .apply(Modifiers.makeVerticalSlope(!top, 45))
                                .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                                .export(quadMap, null);
                    }

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(bottomFace.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .export(quadMap, quadDir);
                }
            }
        }
    }
}
