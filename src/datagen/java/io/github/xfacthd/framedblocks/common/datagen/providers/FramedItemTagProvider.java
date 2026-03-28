package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public final class FramedItemTagProvider extends ItemTagsProvider {
    public FramedItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FramedConstants.MOD_ID);
    }

    @Override
    public String getName() {
        return super.getName() + ": " + FramedConstants.MOD_ID;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.SLABS).add(FBContent.BLOCK_FRAMED_SLAB.value().asItem());
        tag(ItemTags.STAIRS).add(FBContent.BLOCK_FRAMED_STAIRS.value().asItem());
        tag(ItemTags.WALLS).add(FBContent.BLOCK_FRAMED_WALL.value().asItem());
        tag(ItemTags.FENCES).add(FBContent.BLOCK_FRAMED_FENCE.value().asItem());
        tag(ItemTags.DOORS).add(FBContent.BLOCK_FRAMED_DOOR.value().asItem(), FBContent.BLOCK_FRAMED_IRON_DOOR.value().asItem());
        tag(ItemTags.TRAPDOORS).add(FBContent.BLOCK_FRAMED_TRAP_DOOR.value().asItem(), FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value().asItem());
        tag(ItemTags.SIGNS).add(FBContent.BLOCK_FRAMED_SIGN.value().asItem());
        tag(ItemTags.HANGING_SIGNS).add(FBContent.BLOCK_FRAMED_HANGING_SIGN.value().asItem());
        tag(Tags.Items.CHESTS).add(FBContent.BLOCK_FRAMED_CHEST.value().asItem());
        tag(Tags.Items.BOOKSHELVES).add(FBContent.BLOCK_FRAMED_BOOKSHELF.value().asItem());
        tag(Utils.TOOL_WRENCH).add(FBContent.ITEM_FRAMED_WRENCH.value());
        tag(Tags.Items.TOOLS).addTag(Utils.TOOL_WRENCH);
        tag(Utils.DISABLE_INTANGIBLE).add(
                FBContent.ITEM_FRAMED_HAMMER.value(),
                FBContent.ITEM_FRAMED_BLUEPRINT.value(),
                FBContent.ITEM_FRAMED_SCREWDRIVER.value(),
                FBContent.ITEM_FRAMED_KEY.value()
        );

        getOrCreateRawBuilder(Utils.COMPLEX_WRENCH)
                .addOptionalElement(Utils.id("mekanism", "configurator"));

        tag(Utils.CRAFTING_BLOCKED_FLUID_CONTAINERS);
    }
}
