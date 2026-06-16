package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.item.DyeColor;

import java.util.concurrent.CompletableFuture;

public final class FramedOverlayTagProvider extends TagsProvider<BlockOverlay> {
    public FramedOverlayTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, FramedConstants.Registries.BLOCK_OVERLAY_REGISTRY_KEY, registries, FramedConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        TagBuilder builder = getOrCreateRawBuilder(FramedConstants.Tags.OVERLAY_ORDER);
        TagAppender<BlockOverlay> appender = TagAppender.forBuilder(builder);

        appender.add(FramedBlockOverlayProvider.key("grass"))
                .add(FramedBlockOverlayProvider.key("podzol"))
                .add(FramedBlockOverlayProvider.key("mycelium"))
                .add(FramedBlockOverlayProvider.key("path"))
                .add(FramedBlockOverlayProvider.key("crimson_nylium"))
                .add(FramedBlockOverlayProvider.key("warped_nylium"))
                .add(FramedBlockOverlayProvider.key("snow"))
                .add(FramedBlockOverlayProvider.key("moss"));
        for (DyeColor color : DyeColor.values()) {
            appender.add(FramedBlockOverlayProvider.key(color.getName() + "_carpet"));
        }

        long count = registries.lookupOrThrow(registryKey).listElementIds().count();
        if (builder.build().size() != count) {
            throw new IllegalStateException("Overlay order tag size does not match overlay registry size");
        }
    }
}
