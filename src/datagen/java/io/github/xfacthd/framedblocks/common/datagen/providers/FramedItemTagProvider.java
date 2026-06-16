package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.datagen.util.ObjectTagAppender;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
        tag(BlockItemTags.SLABS.item()).add(FBContent.BLOCK_FRAMED_SLAB.value().asItem());
        tag(BlockItemTags.STAIRS.item()).add(FBContent.BLOCK_FRAMED_STAIRS.value().asItem());
        tag(ItemTags.WALLS).add(FBContent.BLOCK_FRAMED_WALL.value().asItem());
        tag(BlockItemTags.FENCES.item()).add(FBContent.BLOCK_FRAMED_FENCE.value().asItem());
        tag(BlockItemTags.DOORS.item()).add(FBContent.BLOCK_FRAMED_DOOR.value().asItem(), FBContent.BLOCK_FRAMED_IRON_DOOR.value().asItem());
        tag(BlockItemTags.TRAPDOORS.item()).add(FBContent.BLOCK_FRAMED_TRAP_DOOR.value().asItem(), FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value().asItem());
        tag(ItemTags.SIGNS).add(FBContent.BLOCK_FRAMED_SIGN.value().asItem());
        tag(ItemTags.HANGING_SIGNS).add(FBContent.BLOCK_FRAMED_HANGING_SIGN.value().asItem());
        tag(Tags.Items.CHESTS).add(FBContent.BLOCK_FRAMED_CHEST.value().asItem());
        tag(Tags.Items.BOOKSHELVES).add(FBContent.BLOCK_FRAMED_BOOKSHELF.value().asItem());
        tag(FramedConstants.Tags.TOOL_WRENCH).add(FBContent.ITEM_FRAMED_WRENCH.value());
        tag(Tags.Items.TOOLS).addTag(FramedConstants.Tags.TOOL_WRENCH);
        tag(FramedConstants.Tags.DISABLE_INTANGIBLE).add(
                FBContent.ITEM_FRAMED_HAMMER.value(),
                FBContent.ITEM_FRAMED_BLUEPRINT.value(),
                FBContent.ITEM_FRAMED_SCREWDRIVER.value(),
                FBContent.ITEM_FRAMED_KEY.value()
        );
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(FBContent.ITEM_FRAMED_AXE.value());
        tag(ItemTags.MINING_ENCHANTABLE).add(FBContent.ITEM_FRAMED_AXE.value());

        getOrCreateRawBuilder(FramedConstants.Tags.COMPLEX_WRENCH)
                .addOptionalElement(Utils.id("mekanism", "configurator"));

        tag(FramedConstants.Tags.CRAFTING_BLOCKED_FLUID_CONTAINERS);
    }

    @Override
    protected ObjectTagAppender<Item> tag(TagKey<Item> tag) {
        return new ObjectTagAppender<>(super.tag(tag), BuiltInRegistries.ITEM);
    }
}
