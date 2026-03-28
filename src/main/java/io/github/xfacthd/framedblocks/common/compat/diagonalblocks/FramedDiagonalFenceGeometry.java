package io.github.xfacthd.framedblocks.common.compat.diagonalblocks;

import fuzs.diagonalblocks.api.v2.block.DiagonalBlock;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.geometry.pillar.FramedFenceGeometry;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

class FramedDiagonalFenceGeometry extends FramedFenceGeometry {
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;

    public FramedDiagonalFenceGeometry(GeometryFactory.Context ctx) {
        super(ctx);
        this.northEast = ctx.state().getValue(DiagonalBlock.NORTH_EAST);
        this.southEast = ctx.state().getValue(DiagonalBlock.SOUTH_EAST);
        this.northWest = ctx.state().getValue(DiagonalBlock.NORTH_WEST);
        this.southWest = ctx.state().getValue(DiagonalBlock.SOUTH_WEST);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        super.transformQuad(quadMap, quad, blockData, modelData);

        createDiagonalFenceBars(quadMap, quad, Direction.NORTH, northEast);
        createDiagonalFenceBars(quadMap, quad, Direction.EAST, southEast);
        createDiagonalFenceBars(quadMap, quad, Direction.SOUTH, southWest);
        createDiagonalFenceBars(quadMap, quad, Direction.WEST, northWest);
    }

    private static void createDiagonalFenceBars(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, boolean active) {
        if (!active) {
            return;
        }

        Direction quadDir = quad.direction();

        if (DirUtils.isY(quadDir)) {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 7F / 16F))
                    .apply(Modifiers.cut(dir.getClockWise(), 9F / 16F))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 9F / 16F))
                    .apply(rotate(dir));

            mod.derive().apply(Modifiers.setPosition(quadDir == Direction.UP ? 15F / 16F : 4F / 16F))
                    .export(quadMap, null);

            mod.apply(Modifiers.setPosition(quadDir == Direction.UP ? 9F / 16F : 10F / 16F))
                    .export(quadMap, null);
        } else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise()) {
            boolean neg = !DirUtils.isPositive(dir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 9F / 16F, 6F / 16F, neg ? 7F / 16F : 1F, 9F / 16F))
                    .apply(Modifiers.setPosition(9F / 16F))
                    .apply(rotate(dir))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 9F / 16F, 12F / 16F, neg ? 7F / 16F : 1F, 15F / 16F))
                    .apply(Modifiers.setPosition(9F / 16F))
                    .apply(rotate(dir))
                    .export(quadMap, null);
        } else if (quadDir == dir) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F / 16F, 6F / 16F, 9F / 16F, 9F / 16F))
                    .apply(rotate(dir))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F / 16F, 12F / 16F, 9F / 16F, 15F / 16F))
                    .apply(rotate(dir))
                    .export(quadMap, null);
        }
    }

    private static QuadModifier.Modifier rotate(Direction dir) {
        return Modifiers.rotateCentered(Direction.Axis.Y, -45F, true, new Vector3f(dir.getStepX(), 1, dir.getStepZ()));
    }
}
