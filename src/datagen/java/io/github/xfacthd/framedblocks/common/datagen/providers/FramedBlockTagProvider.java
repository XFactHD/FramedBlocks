package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.datagen.util.ObjectTagAppender;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class FramedBlockTagProvider extends BlockTagsProvider {
    public FramedBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FramedConstants.MOD_ID);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.SLABS).add(FBContent.BLOCK_FRAMED_SLAB.value());
        tag(BlockTags.STAIRS).add(FBContent.BLOCK_FRAMED_STAIRS.value());
        tag(BlockTags.WALLS).add(FBContent.BLOCK_FRAMED_WALL.value());
        tag(BlockTags.FENCES).add(FBContent.BLOCK_FRAMED_FENCE.value());
        tag(BlockTags.DOORS).add(FBContent.BLOCK_FRAMED_DOOR.value(), FBContent.BLOCK_FRAMED_IRON_DOOR.value());
        tag(BlockTags.TRAPDOORS).add(FBContent.BLOCK_FRAMED_TRAP_DOOR.value(), FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value());
        tag(BlockTags.CLIMBABLE).add(FBContent.BLOCK_FRAMED_LADDER.value());
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
        tag(BlockTags.LIGHTNING_RODS).add(FBContent.BLOCK_FRAMED_LIGHTNING_ROD.value());
        tag(BlockTags.WOODEN_SHELVES).add(FBContent.BLOCK_FRAMED_SHELF.value());

        tag(FramedConstants.Tags.FRAMEABLE).addTags(
                Tags.Blocks.GLASS_BLOCKS,
                BlockTags.ICE,
                BlockTags.LEAVES
        ).addAll(Blocks.COPPER_GRATE.asList());

        getOrCreateRawBuilder(FramedConstants.Tags.FRAMEABLE)
                .addOptionalElement(Utils.id("create", "oak_window"))
                .addOptionalElement(Utils.id("create", "spruce_window"))
                .addOptionalElement(Utils.id("create", "birch_window"))
                .addOptionalElement(Utils.id("create", "jungle_window"))
                .addOptionalElement(Utils.id("create", "acacia_window"))
                .addOptionalElement(Utils.id("create", "dark_oak_window"))
                .addOptionalElement(Utils.id("create", "crimson_window"))
                .addOptionalElement(Utils.id("create", "warped_window"))
                .addOptionalElement(Utils.id("create", "ornate_iron_window"))
                .addOptionalTag(Utils.id("chipped", "glass"))
                .addOptionalTag(Utils.id("chipped", "white_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "orange_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "magenta_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "light_blue_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "yellow_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "lime_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "pink_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "gray_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "light_gray_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "cyan_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "purple_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "blue_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "brown_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "green_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "red_stained_glass"))
                .addOptionalTag(Utils.id("chipped", "black_stained_glass"))
                .addOptionalTag(Utils.id("c", "hardened_glass"));

        tag(FramedConstants.Tags.BLOCK_BLACKLIST).add(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.COMPOSTER
        );

        tag(FramedConstants.Tags.BE_WHITELIST);

        tag(FramedConstants.Tags.NON_OCCLUDEABLE)
                .addTag(BlockTags.LEAVES);

        ObjectTagAppender<Block> fullGroupTag = tag(FramedConstants.Tags.GROUP_FULL_CUBE);
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(b -> !b.hasDynamicShape())
                .map(Block::defaultBlockState)
                .filter(state -> state.hasProperty(FramedProperties.SOLID))
                .map(state -> state.setValue(FramedProperties.SOLID, true))
                .filter(BlockBehaviour.BlockStateBase::isSolidRender)
                .map(BlockBehaviour.BlockStateBase::getBlock)
                .forEach(fullGroupTag::add);
        // Special cases which are not captured by the checks above
        fullGroupTag.add(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.value());

        Set<Block> noToolBlocks = Set.of(
                FBContent.BLOCK_FRAMED_ITEM_FRAME.value(),
                FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.value()
        );

        Set<Block> pickaxeBlocks = new LinkedHashSet<>();

        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_DOOR.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_GATE.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_LIGHTNING_ROD.value());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMING_SAW.value());
        pickaxeBlocks.add(FBContent.BLOCK_POWERED_FRAMING_SAW.value());

        ObjectTagAppender<Block> axeTag = tag(BlockTags.MINEABLE_WITH_AXE);
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(b -> b instanceof IFramedBlock)
                .filter(b -> !noToolBlocks.contains(b))
                .filter(b -> !pickaxeBlocks.contains(b))
                .forEach(axeTag::add);

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pickaxeBlocks.toArray(Block[]::new));

        tag(BlockTags.create(Utils.id("diagonalwindows", "non_diagonal_panes"))).add(FBContent.BLOCK_FRAMED_BARS.value());
    }

    @Override
    protected ObjectTagAppender<Block> tag(TagKey<Block> tag) {
        return new ObjectTagAppender<>(super.tag(tag), BuiltInRegistries.BLOCK);
    }

    @Override
    public String getName() {
        return super.getName() + ": " + FramedConstants.MOD_ID;
    }
}
