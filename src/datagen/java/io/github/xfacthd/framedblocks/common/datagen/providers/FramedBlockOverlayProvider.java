package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlayBuilder;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class FramedBlockOverlayProvider {
    public static void buildBlockOverlayEntries(BootstrapContext<BlockOverlay> context) {
        overlay(context, "moss", (builder, _) -> builder
                .solidTexture("moss")
                .solidFace(BlockOverlay.SolidFace.ALL)
                .sourceItem(Items.MOSS_CARPET)
        );
        overlay(context, "grass", (builder, _) -> builder
                .solidTexture(Utils.id("minecraft", "block/grass_block_top"))
                .edgeTexture(Utils.id("minecraft", "block/grass_block_side_overlay"))
                .solidFace(BlockOverlay.SolidFace.TOP)
                .tintSource(Blocks.GRASS_BLOCK)
                .sourceItem(Items.SHORT_GRASS)
        );
        overlay(context, "podzol", (builder, name) -> builder
                .solidTexture(Utils.id("minecraft", "block/podzol_top"))
                .edgeTexture(name)
                .solidFace(BlockOverlay.SolidFace.TOP)
                .sourceItem(Items.LEAF_LITTER)
        );
        overlay(context, "mycelium", (builder, name) -> builder
                .solidTexture(Utils.id("minecraft", "block/mycelium_top"))
                .edgeTexture(name)
                .solidFace(BlockOverlay.SolidFace.TOP)
                .sourceItem(Items.BROWN_MUSHROOM)
        );
        overlay(context, "path", (builder, name) -> builder
                .solidTexture(Utils.id("minecraft", "block/dirt_path_top"))
                .edgeTexture(name)
                .solidFace(BlockOverlay.SolidFace.TOP)
                .sourceItem(Items.DIRT_PATH)
        );
        overlay(context, "crimson_nylium", (builder, name) -> builder
                .solidTexture(Utils.id("minecraft", "block/" + name))
                .edgeTexture(name)
                .solidFace(BlockOverlay.SolidFace.TOP)
                .sourceItem(Items.CRIMSON_FUNGUS)
        );
        overlay(context, "warped_nylium", (builder, name) -> builder
                .solidTexture(Utils.id("minecraft", "block/" + name))
                .edgeTexture(name)
                .solidFace(BlockOverlay.SolidFace.TOP)
                .sourceItem(Items.WARPED_FUNGUS)
        );
        overlay(context, "snow", (builder, name) -> builder
                .solidTexture(Utils.id("minecraft", "block/" + name))
                .edgeTexture(name)
                .solidFace(BlockOverlay.SolidFace.TOP)
                .sourceItem(Items.SNOWBALL)
        );
        for (DyeColor color : DyeColor.values()) {
            String colName = color.getName();
            overlay(context, colName + "_carpet", (builder, _) -> builder
                    .solidTexture(Utils.id("minecraft", "block/" + colName + "_wool"))
                    .edgeTexture(colName + "_carpet")
                    .solidFace(BlockOverlay.SolidFace.TOP)
                    .sourceItem(BuiltInRegistries.ITEM.get(Utils.id("minecraft", colName + "_carpet")).orElseThrow().value())
            );
        }
    }

    private static void overlay(BootstrapContext<BlockOverlay> context, String name, BuilderOperator operator) {
        BlockOverlayBuilder builder = BlockOverlay.builder(FramedConstants.MOD_ID);
        context.register(key(name), operator.apply(builder, name).build());
    }

    private static ResourceKey<BlockOverlay> key(String name) {
        return ResourceKey.create(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY, Utils.id(name));
    }

    private interface BuilderOperator {
        BlockOverlayBuilder apply(BlockOverlayBuilder builder, String name);
    }

    private FramedBlockOverlayProvider() { }
}
