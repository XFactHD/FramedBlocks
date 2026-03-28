package io.github.xfacthd.framedblocks.client.model.geometry.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedMasonryCornerSegmentGeometry extends Geometry {
    private final Direction dir;
    private final boolean top;

    public FramedMasonryCornerSegmentGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();

        if ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                    .export(quadMap, quadDir);
        } else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .export(quadMap, quadDir);
        } else if (quadDir.getAxis() == dir.getAxis()) {
            boolean inDir = quadDir == dir;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .applyIf(Modifiers.setPosition(.5F), inDir)
                    .export(quadMap, inDir ? null : quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                    .export(quadMap, quadDir);
        } else if (quadDir.getAxis() == dir.getClockWise().getAxis()) {
            boolean inDir = quadDir == dir.getCounterClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F))
                    .applyIf(Modifiers.setPosition(.5F), inDir)
                    .export(quadMap, inDir ? null : quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .apply(Modifiers.cut(dir, .5F))
                    .export(quadMap, quadDir);
        }
    }
}
