package io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SideChainPart;
import org.jspecify.annotations.Nullable;

public record ShelfOverlayProvider(Material.Baked overlayMaterial) implements TemplateOverlayProvider {
    private static final Material OVERLAY_UNPOWERED = new Material(Utils.id("block/shelf_overlay_unpowered"), true);
    private static final Material OVERLAY_UNCONNECTED = new Material(Utils.id("block/shelf_overlay_unconnected"), true);
    private static final Material OVERLAY_RIGHT = new Material(Utils.id("block/shelf_overlay_right"), true);
    private static final Material OVERLAY_CENTER = new Material(Utils.id("block/shelf_overlay_center"), true);
    private static final Material OVERLAY_LEFT = new Material(Utils.id("block/shelf_overlay_left"), true);
    private static final @Nullable Direction[] NULL_FACE = new @Nullable Direction[] { null };
    private static final Factory[] FACTORIES = Util.make(() -> {
        SideChainPart[] chainParts = SideChainPart.values();
        Factory[] factories = new Factory[chainParts.length + 1];
        factories[0] = ctx -> new ShelfOverlayProvider(ctx.materialLookup().getMaterial(OVERLAY_UNPOWERED));
        for (SideChainPart part : chainParts) {
            Material overlay = switch (part) {
                case UNCONNECTED -> OVERLAY_UNCONNECTED;
                case RIGHT -> OVERLAY_RIGHT;
                case CENTER -> OVERLAY_CENTER;
                case LEFT -> OVERLAY_LEFT;
            };
            factories[part.ordinal() + 1] = ctx -> new ShelfOverlayProvider(ctx.materialLookup().getMaterial(overlay));
        }
        return factories;
    });

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData) {
        return true;
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand) {
        generator.generate(NULL_FACE, overlayMaterial, dir -> !DirUtils.isY(dir), null);
    }

    public static Factory factory(BlockState state) {
        if (state.getValue(BlockStateProperties.POWERED)) {
            return FACTORIES[state.getValue(BlockStateProperties.SIDE_CHAIN_PART).ordinal() + 1];
        }
        return FACTORIES[0];
    }
}
