package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.Utils;

public class FramedTagProvider extends BlockTagsProvider
{
    public FramedTagProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, FramedBlocks.MODID, fileHelper); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerTags()
    {
        getOrCreateBuilder(BlockTags.SLABS).add(FBContent.blockFramedSlab.get());
        getOrCreateBuilder(BlockTags.STAIRS).add(FBContent.blockFramedStairs.get());
        getOrCreateBuilder(BlockTags.WALLS).add(FBContent.blockFramedWall.get());
        getOrCreateBuilder(BlockTags.FENCES).add(FBContent.blockFramedFence.get());
        getOrCreateBuilder(BlockTags.DOORS).add(FBContent.blockFramedDoor.get());
        getOrCreateBuilder(BlockTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor.get());
        getOrCreateBuilder(BlockTags.CLIMBABLE).add(FBContent.blockFramedLadder.get());

        getOrCreateBuilder(Utils.FRAMEABLE).addTags(
                Tags.Blocks.GLASS,
                Tags.Blocks.STAINED_GLASS,
                BlockTags.ICE,
                BlockTags.LEAVES
        );

        getOrCreateBuilder(Utils.BLACKLIST).add(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.COMPOSTER
        );
    }
}