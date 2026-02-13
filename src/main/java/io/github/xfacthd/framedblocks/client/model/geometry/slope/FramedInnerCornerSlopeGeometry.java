package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedInnerCornerSlopeGeometry extends Geometry
{
    private final Direction dir;
    private final CornerType type;
    private final boolean ySlope;

    public FramedInnerCornerSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.CORNER_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        if (type.isHorizontal())
        {
            createHorizontalCorner(quadMap, quad);
        }
        else
        {
            createVerticalCorner(quadMap, quad);
        }
    }

    private void createHorizontalCorner(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.direction();
        boolean top = type.isTop();
        boolean right = type.isRight();

        if ((quadDir == Direction.UP && !top) || (quadDir == Direction.DOWN && top))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), right ? 0 : 1, right ? 1 : 0))
                    .export(quadMap.get(quadDir));

            if (ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, right ? 0 : 1, right ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            Direction cutDir = right ? dir.getCounterClockWise() : dir.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, top ? 0 : 1, top ? 1 : 0))
                    .apply(Modifiers.makeHorizontalSlope(right, 45))
                    .export(quadMap.get(null));

            if (!ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, right ? 1 : 0, right ? 0 : 1))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
        }
        else if ((quadDir == dir.getClockWise() && !right) || (quadDir == dir.getCounterClockWise() && right))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));
        }
    }

    private void createVerticalCorner(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.direction();
        boolean top = type.isTop();

        if (quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, top ? 0 : 1, top ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getCounterClockWise(), top ? 0 : 1, top ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 0, 1))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                    .export(quadMap.get(null));
        }
    }
}