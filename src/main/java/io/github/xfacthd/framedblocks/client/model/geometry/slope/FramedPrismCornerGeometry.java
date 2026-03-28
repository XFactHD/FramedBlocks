package io.github.xfacthd.framedblocks.client.model.geometry.slope;

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

public class FramedPrismCornerGeometry extends Geometry {
    private final Direction dir;
    private final boolean top;
    private final boolean offset;
    private final boolean altSlope;

    public FramedPrismCornerGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if ((quadDir == Direction.UP && top) || (quadDir == Direction.DOWN && !top)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 1, 0))
                    .export(quadMap, quadDir);
        } else if (quadDir == dir || quadDir == dir.getCounterClockWise()) {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap, quadDir);
        } else if (!altSlope && quadDir == dir.getOpposite()) {
            if (offset) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(!top, true))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(!top, true))
                        .export(quadMap, null);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutPrismTriangle(!top, true))
                        .export(quadMap, null);
            }
        } else if (altSlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))) {
            if (offset) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(dir, true))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(dir, true))
                        .export(quadMap, null);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutPrismTriangle(dir, true))
                        .export(quadMap, null);
            }
        }
    }
}
