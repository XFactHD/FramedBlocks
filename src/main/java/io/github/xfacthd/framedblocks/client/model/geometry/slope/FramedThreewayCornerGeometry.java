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

public class FramedThreewayCornerGeometry extends Geometry {
    private final Direction dir;
    private final boolean top;
    private final boolean altSlope;

    public FramedThreewayCornerGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
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
        } else if (quadDir == dir.getOpposite()) {
            if (!altSlope) {
                QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(dir.getClockWise()))
                    .apply(Modifiers.makeVerticalSlope(!top, 45))
                    .export(quadMap, null);
            }

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(top ? Direction.DOWN : Direction.UP))
                    .apply(Modifiers.makeHorizontalSlope(false, 45))
                    .export(quadMap, null);
        } else if (!altSlope && quadDir == dir.getClockWise()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(dir.getOpposite()))
                    .apply(Modifiers.makeVerticalSlope(!top, 45))
                    .export(quadMap, null);
        } else if (altSlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(dir.getOpposite()))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSmallTriangle(dir.getClockWise()))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                    .export(quadMap, null);
        }
    }
}
