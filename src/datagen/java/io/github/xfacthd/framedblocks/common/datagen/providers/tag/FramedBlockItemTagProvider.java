package io.github.xfacthd.framedblocks.common.datagen.providers.tag;

import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Holder;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.references.BlockItemId;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Function;

final class FramedBlockItemTagProvider extends BlockItemTagsProvider {
    private FramedBlockItemTagProvider(Function<BlockItemTagId, CombinedAppender> tagSupplier) {
        super(tagSupplier);
    }

    @Override
    protected void run() {
        tag(BlockItemTags.SLABS).add(id(FBContent.BLOCK_FRAMED_SLAB));
        tag(BlockItemTags.STAIRS).add(id(FBContent.BLOCK_FRAMED_STAIRS));
        tag(BlockItemTags.WALLS).add(id(FBContent.BLOCK_FRAMED_WALL));
        tag(BlockItemTags.FENCES).add(id(FBContent.BLOCK_FRAMED_FENCE));
        tag(BlockItemTags.DOORS).add(id(FBContent.BLOCK_FRAMED_DOOR), id(FBContent.BLOCK_FRAMED_IRON_DOOR));
        tag(BlockItemTags.TRAPDOORS).add(id(FBContent.BLOCK_FRAMED_TRAP_DOOR), id(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR));
        tag(BlockItemTags.SIGNS).add(id(FBContent.BLOCK_FRAMED_SIGN));
        tag(BlockItemTags.HANGING_SIGNS).add(id(FBContent.BLOCK_FRAMED_HANGING_SIGN));
        tag(Tags.BlockItems.CHESTS).add(id(FBContent.BLOCK_FRAMED_CHEST));
        tag(Tags.BlockItems.BOOKSHELVES).add(id(FBContent.BLOCK_FRAMED_BOOKSHELF));
        tag(BlockItemTags.RAILS).add(
                id(FBContent.BLOCK_FRAMED_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_FANCY_RAIL),
                id(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL),
                id(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL),
                id(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL),
                id(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE),
                id(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE)
        );
        tag(BlockItemTags.LIGHTNING_RODS).add(id(FBContent.BLOCK_FRAMED_LIGHTNING_ROD));
        tag(BlockItemTags.WOODEN_SHELVES).add(id(FBContent.BLOCK_FRAMED_SHELF));
    }

    private static BlockItemId id(Holder<Block> block) {
        return BlockItemId.create(block.key().identifier(), block.key().identifier());
    }

    static FramedBlockItemTagProvider blocks(Function<TagKey<Block>, BlockItemTagAppender<Block>> tagSupplier) {
        return new FramedBlockItemTagProvider(tagId -> wrapForBlocks(tagSupplier.apply(tagId.block())));
    }

    static FramedBlockItemTagProvider items(Function<TagKey<Item>, BlockItemTagAppender<Item>> tagSupplier) {
        return new FramedBlockItemTagProvider(tagId -> wrapForItems(tagSupplier.apply(tagId.item())));
    }
}
