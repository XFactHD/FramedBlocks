package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.*;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public class FramedItemTagProvider extends ItemTagsProvider
{
    public FramedItemTagProvider(DataGenerator gen, BlockTagsProvider provider, ExistingFileHelper existingFileHelper)
    {
        super(gen, provider, FramedBlocks.MODID, existingFileHelper);
    }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void registerTags()
    {
        getOrCreateBuilder(ItemTags.SLABS).add(FBContent.blockFramedSlab.get().asItem());
        getOrCreateBuilder(ItemTags.STAIRS).add(FBContent.blockFramedStairs.get().asItem());
        getOrCreateBuilder(ItemTags.WALLS).add(FBContent.blockFramedWall.get().asItem());
        getOrCreateBuilder(ItemTags.FENCES).add(FBContent.blockFramedFence.get().asItem());
        getOrCreateBuilder(ItemTags.DOORS).add(FBContent.blockFramedDoor.get().asItem());
        getOrCreateBuilder(ItemTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor.get().asItem());
        getOrCreateBuilder(Tags.Items.CHESTS).add(FBContent.blockFramedChest.get().asItem());
    }
}