package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.*;
import net.minecraft.tags.ItemTags;
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
        getOrCreateBuilder(ItemTags.SLABS).add(FBContent.blockFramedSlab.asItem());
        getOrCreateBuilder(ItemTags.STAIRS).add(FBContent.blockFramedStairs.asItem());
        getOrCreateBuilder(ItemTags.WALLS).add(FBContent.blockFramedWall.asItem());
        getOrCreateBuilder(ItemTags.FENCES).add(FBContent.blockFramedFence.asItem());
        getOrCreateBuilder(ItemTags.DOORS).add(FBContent.blockFramedDoor.asItem());
        getOrCreateBuilder(ItemTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor.asItem());
    }
}