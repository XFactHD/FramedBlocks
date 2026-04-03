package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class FramedShelfGeometry extends Geometry {
    private static final Material OVERLAY_UNPOWERED = new Material(Utils.id("block/shelf_overlay_unpowered"), true);
    private static final Material OVERLAY_UNCONNECTED = new Material(Utils.id("block/shelf_overlay_unconnected"), true);
    private static final Material OVERLAY_RIGHT = new Material(Utils.id("block/shelf_overlay_right"), true);
    private static final Material OVERLAY_CENTER = new Material(Utils.id("block/shelf_overlay_center"), true);
    private static final Material OVERLAY_LEFT = new Material(Utils.id("block/shelf_overlay_left"), true);
    private static final @Nullable Direction[] NULL_FACE = new @Nullable Direction[] { null };

    private final Direction facing;
    private final Material.Baked overlayMaterial;

    public FramedShelfGeometry(GeometryFactory.Context ctx) {
        this.facing = ctx.state().getValue(ShelfBlock.FACING);
        BlockState state = ctx.state();
        Material overlay;
        if (!state.getValue(ShelfBlock.POWERED)) {
            overlay = OVERLAY_UNPOWERED;
        } else {
            overlay = switch (state.getValue(ShelfBlock.SIDE_CHAIN_PART)) {
                case UNCONNECTED -> OVERLAY_UNCONNECTED;
                case RIGHT -> OVERLAY_RIGHT;
                case CENTER -> OVERLAY_CENTER;
                case LEFT -> OVERLAY_LEFT;
            };
        }
        this.overlayMaterial = ctx.materialLookup().getMaterial(overlay);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        if (quadDir == facing) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, 4F/16F))
                    .apply(Modifiers.setPosition(5F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, 4F/16F))
                    .apply(Modifiers.setPosition(5F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.Axis.Y, 12F/16F))
                    .apply(Modifiers.setPosition(3F/16F))
                    .export(quadMap, null);
        } else if (DirUtils.isY(quadDir)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 5F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 5F/16F))
                    .apply(Modifiers.cut(facing.getOpposite(), 13F/16F))
                    .apply(Modifiers.setPosition(4F/16F))
                    .export(quadMap, null);
        } else {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 5F/16F))
                    .apply(Modifiers.cut(Direction.UP, 4F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 5F/16F))
                    .apply(Modifiers.cut(Direction.DOWN, 4F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 3F/16F))
                    .apply(Modifiers.cut(Direction.Axis.Y, 12F/16F))
                    .export(quadMap, quadDir);
        }
    }

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        return true;
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData) {
        generator.generate(NULL_FACE, overlayMaterial, dir -> !DirUtils.isY(dir), null);
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }
}
