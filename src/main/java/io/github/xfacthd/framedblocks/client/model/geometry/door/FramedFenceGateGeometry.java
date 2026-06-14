package io.github.xfacthd.framedblocks.client.model.geometry.door;

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

public class FramedFenceGateGeometry extends Geometry {
    private final Direction dir;
    private final boolean inWall;
    private final boolean open;

    public FramedFenceGateGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.inWall = ctx.state().getValue(BlockStateProperties.IN_WALL);
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        float yOff = inWall ? 3F/16F : 0F;
        if (DirUtils.isY(quadDir)) {
            float quadInset = quadDir == Direction.UP ? 1F - yOff : 11F/16F + yOff;

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), 2F/16F))
                    .apply(Modifiers.cut(dir, 9F/16F))
                    .apply(Modifiers.cut(dir.getOpposite(), 9F/16F))
                    .apply(Modifiers.setPosition(quadInset))
                    .export(quadMap, inWall || quadDir == Direction.DOWN ? null : quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 2F/16F))
                    .apply(Modifiers.cut(dir, 9F/16F))
                    .apply(Modifiers.cut(dir.getOpposite(), 9F/16F))
                    .apply(Modifiers.setPosition(quadInset))
                    .export(quadMap, inWall || quadDir == Direction.DOWN ? null : quadDir);
        } else if (quadDir == dir || quadDir == dir.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), 2F/16F))
                    .apply(Modifiers.cut(Direction.DOWN, 11F/16F + yOff))
                    .apply(Modifiers.cut(Direction.UP, 1F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 2F/16F))
                    .apply(Modifiers.cut(Direction.DOWN, 11F/16F + yOff))
                    .apply(Modifiers.cut(Direction.UP, 1F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);
        } else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise()) {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 5F/16F - yOff, 9F/16F, 1F - yOff));

            mod.derive()
                    .apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap, null);

            mod.export(quadMap, quadDir);
        }

        if (open) {
            createGateOpen(quadMap, quad, yOff);
        } else {
            createGateClosed(quadMap, quad, yOff);
        }
    }

    private void createGateClosed(QuadMapBuilder quadMap, BakedQuad quad, float yOff) {
        Direction quadDir = quad.direction();
        if (quadDir == dir || quadDir == dir.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(2F/16F, 12F/16F - yOff, 14F/16F, 15F/16F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(2F/16F, 6F/16F - yOff, 14F/16F, 9F/16F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(6F/16F, 9F/16F - yOff, 10F/16F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);
        } else if (DirUtils.isY(quadDir)) {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, 9F/16F))
                    .apply(Modifiers.cut(dir.getOpposite(), 9F/16F))
                    .apply(Modifiers.cut(dir.getClockWise(), 14F/16F))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 14F/16F));

            if (mod.isFailed()) {
                return;
            }

            boolean up = quadDir == Direction.UP;
            float height = up ? 9F / 16F - yOff : 4F / 16F + yOff;

            mod.derive().apply(Modifiers.setPosition(up ? 15F/16F - yOff : 10F/16F + yOff))
                    .export(quadMap, null);

            mod.derive().apply(Modifiers.cut(dir.getClockWise(), 6F/16F))
                    .apply(Modifiers.setPosition(height))
                    .export(quadMap, null);

            mod.apply(Modifiers.cut(dir.getCounterClockWise(), 6F/16F))
                    .apply(Modifiers.setPosition(height))
                    .export(quadMap, null);
        } else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 9F/16F - yOff, 9F/16F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap, null);
        }
    }

    private void createGateOpen(QuadMapBuilder quadMap, BakedQuad quad, float yOff) {
        Direction quadDir = quad.direction();
        if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise()) {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 7F/16F))
                    .apply(Modifiers.cut(dir, 15F/16F))
                    .apply(Modifiers.cut(Direction.UP, 15F/16F - yOff))
                    .apply(Modifiers.cut(Direction.DOWN, 10F/16F + yOff));

            QuadModifier topMod = mod.derive()
                    .apply(Modifiers.cut(Direction.DOWN, 4F/16F + yOff));

            topMod.derive().export(quadMap, null);
            topMod.apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap, null);

            QuadModifier botMod = mod.derive()
                    .apply(Modifiers.cut(Direction.UP, 9F/16F - yOff));

            botMod.derive().export(quadMap, null);
            botMod.apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap, null);

            QuadModifier vertMod = mod.apply(Modifiers.cut(Direction.UP, 12F/16F - yOff))
                    .apply(Modifiers.cut(Direction.DOWN, 7F/16F + yOff))
                    .apply(Modifiers.cut(dir.getOpposite(), 3F/16F));
            vertMod.derive().export(quadMap, null);
            vertMod.apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap, null);
        } else if (DirUtils.isY(quadDir)) {
            boolean up = quadDir == Direction.UP;
            float heightOuter = up ? 15F/16F - yOff : 10F/16F + yOff;
            float heightInner = up ? 9F/16F - yOff : 4F/16F + yOff;

            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, 15F/16F))
                    .apply(Modifiers.cut(dir.getOpposite(), 7F/16F));

            QuadModifier leftMod = mod.derive()
                    .apply(Modifiers.cut(dir.getClockWise(), 2F/16F))
                    .apply(Modifiers.setPosition(heightOuter));

            leftMod.derive().export(quadMap, null);
            leftMod.apply(Modifiers.cut(dir, 13F/16F))
                    .apply(Modifiers.setPosition(heightInner))
                    .export(quadMap, null);

            QuadModifier rightMod = mod
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 2F/16F))
                    .apply(Modifiers.setPosition(heightOuter));

            rightMod.derive().export(quadMap, null);
            rightMod.apply(Modifiers.cut(dir, 13F/16F))
                    .apply(Modifiers.setPosition(heightInner))
                    .export(quadMap, null);
        } else if (quadDir == dir) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(0F, 6F/16F - yOff, 2F/16F, 15F/16F - yOff))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(14F/16F, 6F/16F - yOff, 1F, 15F/16F - yOff))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap, null);
        } else if (quadDir == dir.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(0F, 9F/16F - yOff, 2F/16F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(3F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(14F/16F, 9F/16F - yOff, 1F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(3F/16F))
                    .export(quadMap, null);
        }
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }
}
