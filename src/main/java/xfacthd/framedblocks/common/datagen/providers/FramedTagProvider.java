package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public class FramedTagProvider extends BlockTagsProvider
{
    public FramedTagProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void registerTags()
    {
        getBuilder(BlockTags.SLABS).add(FBContent.blockFramedSlab);
        getBuilder(BlockTags.STAIRS).add(FBContent.blockFramedStairs);
        getBuilder(BlockTags.WALLS).add(FBContent.blockFramedWall);
        getBuilder(BlockTags.FENCES).add(FBContent.blockFramedFence);
        //getBuilder(BlockTags.DOORS).add(FBContent.blockFramedDoor);
        //getBuilder(BlockTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor);
    }
}