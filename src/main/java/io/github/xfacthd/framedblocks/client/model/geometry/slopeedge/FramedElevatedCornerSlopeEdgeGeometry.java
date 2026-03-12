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

public class FramedElevatedCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final CornerType type;
    private final boolean altSlope;

    public FramedElevatedCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.CORNER_TYPE);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (type.isHorizontal())
        {
            boolean top = type.isTop();
            boolean right = type.isRight();
            Direction xBackFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            Direction yBackFace = top ? Direction.UP : Direction.DOWN;
            if (quadDir == dir.getOpposite())
            {
                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(yBackFace, .5F))
                            .apply(Modifiers.cut(xBackFace.getOpposite(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(xBackFace, .5F))
                            .apply(Modifiers.cut(yBackFace.getOpposite(), right ? 1F : 0F, right ? 0F : 1F))
                            .apply(Modifiers.makeHorizontalSlope(right, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                        .apply(Modifiers.cut(xBackFace.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == xBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(yBackFace.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(xBackFace, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), right ? .5F : 1.5F, right ? 1.5F : .5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(xBackFace.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == xBackFace.getOpposite())
            {
                if (altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(yBackFace.getOpposite(), right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .apply(Modifiers.makeHorizontalSlope(!right, 45))
                            .apply(Modifiers.offset(xBackFace.getOpposite(), .5F))
                            .export(quadMap, null);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == yBackFace.getOpposite())
            {
                if (altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(xBackFace.getOpposite(), right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap, null);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;
            Direction topDir = top ? Direction.DOWN : Direction.UP;
            if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(topDir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(topDir, .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(topDir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(topDir, .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getOpposite())
            {
                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(topDir.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap, null);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(topDir, .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == dir.getClockWise())
            {
                if (!altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(topDir.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap, null);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(topDir, .5F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir == topDir)
            {
                if (altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(dir.getClockWise(), 1F, 0F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(topDir, .5F))
                            .export(quadMap, null);

                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cut(dir.getOpposite(), 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(topDir, .5F))
                            .export(quadMap, null);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .export(quadMap, quadDir);
            }
        }
    }
}
