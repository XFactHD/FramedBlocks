package io.github.xfacthd.framedblocks.client.model.geometry.cube;

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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedTubeGeometry extends Geometry {
    private final Direction.Axis axis;
    private final float thickness;

    public FramedTubeGeometry(GeometryFactory.Context ctx) {
        this.axis = ctx.state().getValue(BlockStateProperties.AXIS);
        this.thickness = (ctx.state().getValue(PropertyHolder.THICK) ? 3F : 2F) / 16F;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (axis == Direction.Axis.Y) {
            if (quadDir.getAxis() == axis) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, thickness))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, thickness))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.EAST, thickness))
                        .apply(Modifiers.cut(Direction.Axis.Z, 1F - thickness))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.WEST, thickness))
                        .apply(Modifiers.cut(Direction.Axis.Z, 1F - thickness))
                        .export(quadMap, quadDir);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 1F - thickness))
                        .apply(Modifiers.setPosition(thickness))
                        .export(quadMap, null);
            }
        } else {
            if (quadDir.getAxis() == axis) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, thickness))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, thickness))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise(), thickness))
                        .apply(Modifiers.cut(Direction.Axis.Y, 1F - thickness))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), thickness))
                        .apply(Modifiers.cut(Direction.Axis.Y, 1F - thickness))
                        .export(quadMap, quadDir);
            } else if (DirUtils.isY(quadDir)) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(DirUtils.getPerpendicularAxis(axis, Direction.Axis.Y), 1F - thickness))
                        .apply(Modifiers.setPosition(thickness))
                        .export(quadMap, null);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.Axis.Y, 1F - thickness))
                        .apply(Modifiers.setPosition(thickness))
                        .export(quadMap, null);
            }
        }
    }

    @Override
    public boolean transformAllQuads() {
        return true;
    }
}
