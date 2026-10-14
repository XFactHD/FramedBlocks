package io.github.xfacthd.framedblocks.common.datagen.providers.tag;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
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
    protected void addTags(HolderLookup.Provider provider) {
        FramedBlockItemTagProvider.items(this::tag).run();

        tag(FramedConstants.Tags.TOOL_WRENCH).add(FBContent.ITEM_FRAMED_WRENCH.key());
        tag(Tags.Items.TOOLS).addTag(FramedConstants.Tags.TOOL_WRENCH);
        tag(FramedConstants.Tags.DISABLE_INTANGIBLE).add(
                FBContent.ITEM_FRAMED_HAMMER.key(),
                FBContent.ITEM_FRAMED_BLUEPRINT.key(),
                FBContent.ITEM_FRAMED_SCREWDRIVER.key(),
                FBContent.ITEM_FRAMED_KEY.key()
        );
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(FBContent.ITEM_FRAMED_AXE.key());
        tag(ItemTags.MINING_ENCHANTABLE).add(FBContent.ITEM_FRAMED_AXE.key());

        tag(FramedConstants.Tags.COMPLEX_WRENCH)
                .addOptional(key("mekanism", "configurator"));

        tag(FramedConstants.Tags.CRAFTING_BLOCKED_FLUID_CONTAINERS);
    }

    @SuppressWarnings("SameParameterValue")
    private ResourceKey<Item> key(String modId, String path) {
        return ResourceKey.create(registryKey, Utils.id(modId, path));
    }

    @Override
    protected BlockItemTagAppender<Item> tag(TagKey<Item> tag) {
        return new BlockItemTagAppender<>(super.tag(tag)) {
            @Override
            protected ResourceKey<Item> convertElement(BlockItemId element) {
                return element.item();
            }
        };
    }

    @Override
    public String getName() {
        return super.getName() + ": " + FramedConstants.MOD_ID;
    }
}
