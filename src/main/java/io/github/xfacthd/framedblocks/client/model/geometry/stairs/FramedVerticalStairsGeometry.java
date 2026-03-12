package io.github.xfacthd.framedblocks.client.model.geometry.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedVerticalStairsGeometry extends Geometry
{
    private final boolean vertical;
    private final boolean top;
    private final boolean bottom;
    private final boolean forward;
    private final boolean counterClockWise;
    private final Direction dir;

    public FramedVerticalStairsGeometry(GeometryFactory.Context ctx)
    {
        StairsType type = ctx.state().getValue(PropertyHolder.STAIRS_TYPE);
        this.vertical = type == StairsType.VERTICAL;
        this.top = type.isTop();
        this.bottom = type.isBottom();
        this.forward = type.isForward();
        this.counterClockWise = type.isCounterClockwise();
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        Direction vertEdge = bottom ? Direction.DOWN : Direction.UP;

        if (vertical && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir.getOpposite(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }

        if ((quadDir == Direction.UP && !top) || (quadDir == Direction.DOWN && !bottom))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .export(quadMap, quadDir);
        }

        if (quadDir == dir.getOpposite() && !vertical)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.cut(vertEdge, .5F), counterClockWise)
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                    .applyIf(Modifiers.cut(vertEdge, .5F), forward)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);

            if (counterClockWise)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.cut(vertEdge.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }
        }

        if (quadDir == dir.getClockWise() && !vertical)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.cut(vertEdge, .5F), forward)
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .applyIf(Modifiers.cut(vertEdge, .5F), counterClockWise)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);

            if (forward)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.cut(vertEdge.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }
        }

        if ((quadDir == Direction.UP && top) || (quadDir == Direction.DOWN && bottom))
        {
            QuadModifier.of(quad)
                    .applyIf(Modifiers.cut(dir.getOpposite(), .5F), counterClockWise)
                    .applyIf(Modifiers.cut(dir.getClockWise(), .5F), forward)
                    .export(quadMap, quadDir);

            if (forward)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }

            if (counterClockWise)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }
        }

        if (quadDir == dir && forward)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .export(quadMap, quadDir);
        }

        if (quadDir == dir.getCounterClockWise() && counterClockWise)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .export(quadMap, quadDir);
        }
    }
}
