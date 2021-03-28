package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public class FramedTagProvider extends BlockTagsProvider
{
    public FramedTagProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, FramedBlocks.MODID, fileHelper); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void registerTags()
    {
        getOrCreateBuilder(BlockTags.SLABS).add(FBContent.blockFramedSlab);
        getOrCreateBuilder(BlockTags.STAIRS).add(FBContent.blockFramedStairs);
        getOrCreateBuilder(BlockTags.WALLS).add(FBContent.blockFramedWall);
        getOrCreateBuilder(BlockTags.FENCES).add(FBContent.blockFramedFence);
        getOrCreateBuilder(BlockTags.DOORS).add(FBContent.blockFramedDoor);
        getOrCreateBuilder(BlockTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor);
    }
}