package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerTubeOrientation;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedCornerTubeGeometry extends Geometry {
    private static final Direction[] DIRECTIONS = Direction.values();

    private final CornerTubeOrientation orientation;
    private final float minWidth;
    private final float maxWidth;

    public FramedCornerTubeGeometry(GeometryFactory.Context ctx) {
        this.orientation = ctx.state().getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
        float thickness = ctx.state().getValue(PropertyHolder.THICK) ? 3F : 2F;
        this.minWidth = thickness / 16F;
        this.maxWidth = (16F - thickness) / 16F;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();

        if (DirUtils.isY(quadDir)) {
            if (orientation.isSideOpen(quadDir)) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, minWidth))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, minWidth))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.EAST, minWidth))
                        .apply(Modifiers.cut(Direction.Axis.Z, maxWidth))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.WEST, minWidth))
                        .apply(Modifiers.cut(Direction.Axis.Z, maxWidth))
                        .export(quadMap, quadDir);

                QuadModifier mod = QuadModifier.of(quad);
                for (Direction side : DIRECTIONS) {
                    if (side.getAxis() != quadDir.getAxis() && !orientation.isSideOpen(side)) {
                        mod.apply(Modifiers.cut(side, maxWidth));
                    }
                }
                mod.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
            } else if (orientation.isSideOpen(quadDir.getOpposite())) {
                QuadModifier mod = QuadModifier.of(quad);
                for (Direction side : DIRECTIONS) {
                    if (side.getAxis() == quadDir.getAxis()) {
                        continue;
                    }

                    if (orientation.isSideOpen(side.getOpposite())) {
                        mod.apply(Modifiers.cut(side, minWidth));
                    } else if (!orientation.isSideOpen(side)) {
                        mod.apply(Modifiers.cut(side, maxWidth));
                    }
                }
                mod.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
            } else {
                QuadModifier modOne = QuadModifier.of(quad);
                QuadModifier modTwo = QuadModifier.of(quad);
                for (Direction side : DIRECTIONS) {
                    if (side.getAxis() == quadDir.getAxis()) {
                        continue;
                    }

                    if (side != orientation.getPrimaryDir()) {
                        modOne.apply(Modifiers.cut(side, maxWidth));
                    }
                    if (side != orientation.getSecondaryDir()) {
                        float len = side == orientation.getSecondaryDir().getOpposite() ? minWidth : maxWidth;
                        modTwo.apply(Modifiers.cut(side, len));
                    }
                }
                modOne.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
                modTwo.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
            }
        } else {
            if (orientation.isSideOpen(quadDir)) {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, minWidth))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, minWidth))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise(), minWidth))
                        .apply(Modifiers.cut(Direction.Axis.Y, maxWidth))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), minWidth))
                        .apply(Modifiers.cut(Direction.Axis.Y, maxWidth))
                        .export(quadMap, quadDir);

                QuadModifier mod = QuadModifier.of(quad);
                for (Direction side : DIRECTIONS) {
                    if (side.getAxis() != quadDir.getAxis() && !orientation.isSideOpen(side)) {
                        mod.apply(Modifiers.cut(side, maxWidth, maxWidth));
                    }
                }
                mod.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
            } else if (orientation.isSideOpen(quadDir.getOpposite())) {
                QuadModifier mod = QuadModifier.of(quad);
                for (Direction side : DIRECTIONS) {
                    if (side.getAxis() == quadDir.getAxis()) {
                        continue;
                    }

                    if (orientation.isSideOpen(side.getOpposite())) {
                        mod.apply(Modifiers.cut(side, minWidth, minWidth));
                    } else if (!orientation.isSideOpen(side)) {
                        mod.apply(Modifiers.cut(side, maxWidth, maxWidth));
                    }
                }
                mod.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
            } else {
                QuadModifier modOne = QuadModifier.of(quad);
                QuadModifier modTwo = QuadModifier.of(quad);
                for (Direction side : DIRECTIONS) {
                    if (side.getAxis() == quadDir.getAxis()) {
                        continue;
                    }

                    if (side != orientation.getPrimaryDir()) {
                        modOne.apply(Modifiers.cut(side, maxWidth, maxWidth));
                    }
                    if (side != orientation.getSecondaryDir()) {
                        float len = side == orientation.getSecondaryDir().getOpposite() ? minWidth : maxWidth;
                        modTwo.apply(Modifiers.cut(side, len, len));
                    }
                }
                modOne.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
                modTwo.apply(Modifiers.setPosition(minWidth)).export(quadMap, null);
            }
        }
    }

    @Override
    public boolean transformAllQuads() {
        return true;
    }
}
