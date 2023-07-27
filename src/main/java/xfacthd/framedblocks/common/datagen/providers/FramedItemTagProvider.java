package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.util.Utils;

import java.util.concurrent.CompletableFuture;

public final class FramedItemTagProvider extends ItemTagsProvider
{
    public FramedItemTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTagProvider,
            ExistingFileHelper fileHelper
    )
    {
        super(output, lookupProvider, blockTagProvider, FramedConstants.MOD_ID, fileHelper);
    }

    @Override
    public String getName()
    {
        return super.getName() + ": " + FramedConstants.MOD_ID;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(ItemTags.SLABS).add(FBContent.BLOCK_FRAMED_SLAB.get().asItem());
        tag(ItemTags.STAIRS).add(FBContent.BLOCK_FRAMED_STAIRS.get().asItem());
        tag(ItemTags.WALLS).add(FBContent.BLOCK_FRAMED_WALL.get().asItem());
        tag(ItemTags.FENCES).add(FBContent.BLOCK_FRAMED_FENCE.get().asItem());
        tag(ItemTags.DOORS).add(FBContent.BLOCK_FRAMED_DOOR.get().asItem(), FBContent.BLOCK_FRAMED_IRON_DOOR.get().asItem());
        tag(ItemTags.TRAPDOORS).add(FBContent.BLOCK_FRAMED_TRAP_DOOR.get().asItem(), FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get().asItem());
        tag(ItemTags.SIGNS).add(FBContent.BLOCK_FRAMED_SIGN.get().asItem());
        tag(ItemTags.HANGING_SIGNS).add(FBContent.BLOCK_FRAMED_HANGING_SIGN.get().asItem());
        tag(Tags.Items.CHESTS).add(FBContent.BLOCK_FRAMED_CHEST.get().asItem());
        tag(Utils.WRENCH).add(FBContent.ITEM_FRAMED_WRENCH.get());
        tag(Utils.DISABLE_INTANGIBLE).addTag(Utils.WRENCH).add(
                FBContent.ITEM_FRAMED_HAMMER.get(),
                FBContent.ITEM_FRAMED_BLUEPRINT.get(),
                FBContent.ITEM_FRAMED_SCREWDRIVER.get(),
                FBContent.ITEM_FRAMED_KEY.get()
        );
    }
}