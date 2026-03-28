package io.github.xfacthd.framedblocks.client.model.geometry.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedCheckeredSlabSegmentGeometry extends Geometry {
    private final boolean top;
    private final boolean second;

    public FramedCheckeredSlabSegmentGeometry(GeometryFactory.Context ctx) {
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.second = ctx.state().getValue(PropertyHolder.SECOND);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir)) {
            boolean up = quadDir == Direction.UP;
            Direction xDir = (second ^ up) ? Direction.WEST : Direction.EAST;

            if (up == top) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, .5F))
                        .apply(Modifiers.cut(xDir, .5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, .5F))
                        .apply(Modifiers.cut(xDir.getOpposite(), .5F))
                        .export(quadMap, quadDir);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, .5F))
                        .apply(Modifiers.cut(xDir.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, .5F))
                        .apply(Modifiers.cut(xDir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }
        } else {
            Direction horDir = DirUtils.isX(quadDir) ^ second ? quadDir.getCounterClockWise() : quadDir.getClockWise();

            if (!top) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, .5F))
                        .apply(Modifiers.cut(horDir, .5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, .5F))
                        .apply(Modifiers.cut(horDir.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, .5F))
                        .apply(Modifiers.cut(horDir.getOpposite(), .5F))
                        .export(quadMap, quadDir);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, .5F))
                        .apply(Modifiers.cut(horDir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }
        }
    }
}
