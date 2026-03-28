package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedFenceGeometry extends Geometry {
    private final boolean north;
    private final boolean east;
    private final boolean south;
    private final boolean west;

    public FramedFenceGeometry(GeometryFactory.Context ctx) {
        this.north = ctx.state().getValue(BlockStateProperties.NORTH);
        this.east = ctx.state().getValue(BlockStateProperties.EAST);
        this.south = ctx.state().getValue(BlockStateProperties.SOUTH);
        this.west = ctx.state().getValue(BlockStateProperties.WEST);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                    .export(quadMap, quadDir);
        } else {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(quadDir.getClockWise(), 10F/16F))
                    .apply(Modifiers.cut(quadDir.getCounterClockWise(), 10F/16F))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap, null);
        }

        createFenceBars(quadMap, quad, Direction.NORTH, north);
        createFenceBars(quadMap, quad, Direction.EAST, east);
        createFenceBars(quadMap, quad, Direction.SOUTH, south);
        createFenceBars(quadMap, quad, Direction.WEST, west);
    }

    private static void createFenceBars(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, boolean active) {
        if (!active) {
            return;
        }

        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir)) {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 6F/16F))
                    .apply(Modifiers.cut(dir.getClockWise(), 9F/16F))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 9F/16F));

            mod.derive().apply(Modifiers.setPosition(quadDir == Direction.UP ? 15F/16F : 4F/16F))
                    .export(quadMap, null);

            mod.apply(Modifiers.setPosition(quadDir == Direction.UP ? 9F/16F : 10F/16F))
                    .export(quadMap, null);
        } else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise()) {
            boolean neg = !DirUtils.isPositive(dir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 10F/16F, 6F/16F, neg ? 6F/16F : 1F, 9F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 10F/16F, 12F/16F, neg ? 6F/16F : 1F, 15F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);
        } else if (quadDir == dir) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 6F/16F, 9F/16F, 9F/16F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 12F/16F, 9F/16F, 15F/16F))
                    .export(quadMap, quadDir);
        }
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }
}
