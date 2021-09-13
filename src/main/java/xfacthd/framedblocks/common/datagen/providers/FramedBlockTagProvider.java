package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fmllegacy.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.util.Utils;

public class FramedBlockTagProvider extends BlockTagsProvider
{
    public FramedBlockTagProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, FramedBlocks.MODID, fileHelper); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags()
    {
        tag(BlockTags.SLABS).add(FBContent.blockFramedSlab.get());
        tag(BlockTags.STAIRS).add(FBContent.blockFramedStairs.get());
        tag(BlockTags.WALLS).add(FBContent.blockFramedWall.get());
        tag(BlockTags.FENCES).add(FBContent.blockFramedFence.get());
        tag(BlockTags.DOORS).add(FBContent.blockFramedDoor.get());
        tag(BlockTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor.get());
        tag(BlockTags.CLIMBABLE).add(FBContent.blockFramedLadder.get());
        tag(Tags.Blocks.CHESTS).add(FBContent.blockFramedChest.get());
        tag(BlockTags.RAILS).add(FBContent.blockFramedRailSlope.get());

        tag(Utils.FRAMEABLE).addTags(
                Tags.Blocks.GLASS,
                Tags.Blocks.STAINED_GLASS,
                BlockTags.ICE,
                BlockTags.LEAVES
        );

        tag(Utils.BLACKLIST).add(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.COMPOSTER
        );

        TagsProvider.TagAppender<Block> axeTag = tag(BlockTags.MINEABLE_WITH_AXE);
        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(b -> b instanceof IFramedBlock)
                .forEach(axeTag::add);
    }
}