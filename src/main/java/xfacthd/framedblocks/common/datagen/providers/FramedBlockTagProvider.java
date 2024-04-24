package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class FramedBlockTagProvider extends BlockTagsProvider
{
    public FramedBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper)
    {
        super(output, lookupProvider, FramedConstants.MOD_ID, fileHelper);
    }

    @Override
    public String getName() { return super.getName() + ": " + FramedConstants.MOD_ID; }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(BlockTags.SLABS).add(FBContent.BLOCK_FRAMED_SLAB.value());
        tag(BlockTags.STAIRS).add(FBContent.BLOCK_FRAMED_STAIRS.value());
        tag(BlockTags.WALLS).add(FBContent.BLOCK_FRAMED_WALL.value());
        tag(BlockTags.FENCES).add(FBContent.BLOCK_FRAMED_FENCE.value());
        tag(BlockTags.DOORS).add(FBContent.BLOCK_FRAMED_DOOR.value(), FBContent.BLOCK_FRAMED_IRON_DOOR.value());
        tag(BlockTags.TRAPDOORS).add(FBContent.BLOCK_FRAMED_TRAP_DOOR.value(), FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value());
        tag(BlockTags.CLIMBABLE).add(FBContent.BLOCK_FRAMED_LADDER.value());
        tag(BlockTags.SIGNS).add(FBContent.BLOCK_FRAMED_SIGN.value(), FBContent.BLOCK_FRAMED_WALL_SIGN.value());
        tag(BlockTags.STANDING_SIGNS).add(FBContent.BLOCK_FRAMED_SIGN.value());
        tag(BlockTags.WALL_SIGNS).add(FBContent.BLOCK_FRAMED_WALL_SIGN.value());
        tag(BlockTags.CEILING_HANGING_SIGNS).add(FBContent.BLOCK_FRAMED_HANGING_SIGN.value());
        tag(BlockTags.WALL_HANGING_SIGNS).add(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN.value());
        tag(Tags.Blocks.CHESTS).add(FBContent.BLOCK_FRAMED_CHEST.value());
        tag(BlockTags.RAILS).add(
                FBContent.BLOCK_FRAMED_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_FANCY_RAIL.value(),
                FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value(),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value(),
                FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value(),
                FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.value(),
                FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.value()
        );
        tag(BlockTags.ENCHANTMENT_POWER_PROVIDER).add(FBContent.BLOCK_FRAMED_BOOKSHELF.value());
        tag(Tags.Blocks.BOOKSHELVES).add(FBContent.BLOCK_FRAMED_BOOKSHELF.value());

        TagsProvider.TagAppender<Block> frameable = tag(Utils.FRAMEABLE).addTags(
                Tags.Blocks.GLASS_BLOCKS,
                Tags.Blocks.GLASS_BLOCKS_COLORLESS,
                BlockTags.ICE,
                BlockTags.LEAVES
        );

        frameable.addOptional(rl("create", "oak_window"))
                 .addOptional(rl("create", "spruce_window"))
                 .addOptional(rl("create", "birch_window"))
                 .addOptional(rl("create", "jungle_window"))
                 .addOptional(rl("create", "acacia_window"))
                 .addOptional(rl("create", "dark_oak_window"))
                 .addOptional(rl("create", "crimson_window"))
                 .addOptional(rl("create", "warped_window"))
                 .addOptional(rl("create", "ornate_iron_window"))
                 .addOptionalTag(rl("chipped", "glass"))
                 .addOptionalTag(rl("chipped", "white_stained_glass"))
                 .addOptionalTag(rl("chipped", "orange_stained_glass"))
                 .addOptionalTag(rl("chipped", "magenta_stained_glass"))
                 .addOptionalTag(rl("chipped", "light_blue_stained_glass"))
                 .addOptionalTag(rl("chipped", "yellow_stained_glass"))
                 .addOptionalTag(rl("chipped", "lime_stained_glass"))
                 .addOptionalTag(rl("chipped", "pink_stained_glass"))
                 .addOptionalTag(rl("chipped", "gray_stained_glass"))
                 .addOptionalTag(rl("chipped", "light_gray_stained_glass"))
                 .addOptionalTag(rl("chipped", "cyan_stained_glass"))
                 .addOptionalTag(rl("chipped", "purple_stained_glass"))
                 .addOptionalTag(rl("chipped", "blue_stained_glass"))
                 .addOptionalTag(rl("chipped", "brown_stained_glass"))
                 .addOptionalTag(rl("chipped", "green_stained_glass"))
                 .addOptionalTag(rl("chipped", "red_stained_glass"))
                 .addOptionalTag(rl("chipped", "black_stained_glass"))
                 .addOptionalTag(rl("forge", "hardened_glass"));

        tag(Utils.BLOCK_BLACKLIST).add(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.COMPOSTER
        );

        tag(Utils.BE_WHITELIST);

        tag(Utils.CAMO_SUSTAIN_PLANT).add(
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL
        ).addTags(
                BlockTags.DIRT,
                BlockTags.SAND,
                BlockTags.NYLIUM
        );

        Set<Block> noToolBlocks = Set.of(
                FBContent.BLOCK_FRAMED_ITEM_FRAME.value(),
                FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.value()
        );

        Set<Block> pickaxeBlocks = new LinkedHashSet<>();

        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_DOOR.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_GATE.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMING_SAW.value());
        pickaxeBlocks.add(FBContent.BLOCK_POWERED_FRAMING_SAW.value());

        IntrinsicTagAppender<Block> axeTag = tag(BlockTags.MINEABLE_WITH_AXE);
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(b -> b instanceof IFramedBlock)
                .filter(b -> !noToolBlocks.contains(b))
                .filter(b -> !pickaxeBlocks.contains(b))
                .forEach(axeTag::add);

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pickaxeBlocks.toArray(Block[]::new));

        tag(BlockTags.create(rl("diagonalwindows", "non_diagonal_panes"))).add(FBContent.BLOCK_FRAMED_BARS.value());
    }



    private static ResourceLocation rl(String modid, String path)
    {
        return new ResourceLocation(modid, path);
    }
}