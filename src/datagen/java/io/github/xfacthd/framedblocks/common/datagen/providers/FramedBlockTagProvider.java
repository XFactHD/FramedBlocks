package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
    protected void addTags(HolderLookup.Provider provider) {
        FramedBlockItemTagProvider.blocks(this::tag).run();

        tag(BlockTags.CLIMBABLE).add(FBContent.BLOCK_FRAMED_LADDER.key());
        tag(BlockTags.WALL_SIGNS).add(FBContent.BLOCK_FRAMED_WALL_SIGN.key());
        tag(BlockTags.WALL_HANGING_SIGNS).add(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN.key());
        tag(BlockTags.ENCHANTMENT_POWER_PROVIDER).add(FBContent.BLOCK_FRAMED_BOOKSHELF.key());

        tag(FramedConstants.Tags.FRAMEABLE)
                .addTag(Tags.Blocks.GLASS_BLOCKS)
                .addTag(BlockTags.ICE)
                .addTag(BlockTags.LEAVES)
                .addAll(BlockItemIds.COPPER_GRATE.map(BlockItemId::block))
                .addOptional(key("create", "oak_window"))
                .addOptional(key("create", "spruce_window"))
                .addOptional(key("create", "birch_window"))
                .addOptional(key("create", "jungle_window"))
                .addOptional(key("create", "acacia_window"))
                .addOptional(key("create", "dark_oak_window"))
                .addOptional(key("create", "crimson_window"))
                .addOptional(key("create", "warped_window"))
                .addOptional(key("create", "ornate_iron_window"))
                .addOptionalTag(Utils.blockTag("chipped", "glass"))
                .addOptionalTag(Utils.blockTag("chipped", "white_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "orange_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "magenta_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "light_blue_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "yellow_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "lime_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "pink_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "gray_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "light_gray_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "cyan_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "purple_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "blue_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "brown_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "green_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "red_stained_glass"))
                .addOptionalTag(Utils.blockTag("chipped", "black_stained_glass"))
                .addOptionalTag(Utils.blockTag("c", "hardened_glass"));

        tag(FramedConstants.Tags.BLOCK_BLACKLIST).add(
                BlockItemIds.PISTON.block(),
                BlockItemIds.STICKY_PISTON.block(),
                BlockItemIds.COMPOSTER.block()
        );

        tag(FramedConstants.Tags.BE_WHITELIST);

        tag(FramedConstants.Tags.NON_OCCLUDEABLE)
                .addTag(BlockTags.LEAVES);

        TagAppender<Block> fullGroupTag = tag(FramedConstants.Tags.GROUP_FULL_CUBE);
        for (Holder<Block> block : FBContent.getRegisteredBlocks()) {
            BlockState state = block.value().defaultBlockState();
            if (!state.getBlock().hasDynamicShape() && state.hasProperty(FramedProperties.SOLID) && state.setValue(FramedProperties.SOLID, true).isSolidRender()) {
                fullGroupTag.add(block.key());
            }
        }
        // Special cases which are not captured by the checks above
        fullGroupTag.add(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.key());

        Set<ResourceKey<Block>> noToolBlocks = Set.of(
                FBContent.BLOCK_FRAMED_ITEM_FRAME.key(),
                FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.key()
        );

        Set<ResourceKey<Block>> pickaxeBlocks = new LinkedHashSet<>();

        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_DOOR.key());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.key());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_IRON_GATE.key());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMED_LIGHTNING_ROD.key());
        pickaxeBlocks.add(FBContent.BLOCK_FRAMING_SAW.key());
        pickaxeBlocks.add(FBContent.BLOCK_POWERED_FRAMING_SAW.key());

        TagAppender<Block> axeTag = tag(BlockTags.MINEABLE_WITH_AXE);
        FBContent.getRegisteredBlocks()
                .stream()
                .filter(b -> b.value() instanceof IFramedBlock)
                .map(Holder::key)
                .filter(b -> !noToolBlocks.contains(b))
                .filter(b -> !pickaxeBlocks.contains(b))
                .forEach(axeTag::add);

        TagAppender<Block> pickaxeTag = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        pickaxeBlocks.forEach(pickaxeTag::add);

        Set<ResourceKey<Block>> nonMotionBlockingBlocks = Set.of(
                FBContent.BLOCK_FRAMED_FANCY_RAIL.key(),
                FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.key(),
                FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.key(),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.key(),
                FBContent.BLOCK_FRAMED_TORCH.key(),
                FBContent.BLOCK_FRAMED_WALL_TORCH.key(),
                FBContent.BLOCK_FRAMED_SOUL_TORCH.key(),
                FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH.key(),
                FBContent.BLOCK_FRAMED_COPPER_TORCH.key(),
                FBContent.BLOCK_FRAMED_COPPER_WALL_TORCH.key(),
                FBContent.BLOCK_FRAMED_REDSTONE_TORCH.key(),
                FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH.key(),
                FBContent.BLOCK_FRAMED_SIGN.key(),
                FBContent.BLOCK_FRAMED_WALL_SIGN.key(),
                FBContent.BLOCK_FRAMED_LADDER.key(),
                FBContent.BLOCK_FRAMED_LEVER.key(),
                FBContent.BLOCK_FRAMED_BUTTON.key(),
                FBContent.BLOCK_FRAMED_STONE_BUTTON.key(),
                FBContent.BLOCK_FRAMED_LARGE_BUTTON.key(),
                FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON.key(),
                FBContent.BLOCK_FRAMED_FLOWER_POT.key(),
                FBContent.BLOCK_FRAMED_MINI_CUBE.key(),
                FBContent.BLOCK_FRAMED_CORNER_STRIP.key()
        );
        TagAppender<Block> motionBlockingTag = tag(BlockTags.BLOCKS_MOTION_NO_LEAVES);
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::key)
                .filter(b -> !nonMotionBlockingBlocks.contains(b))
                .forEach(motionBlockingTag::add);

        tag(BlockTags.BLOCKS_FLUID_FLOW)
                .remove(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.key())
                .remove(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.key())
                .remove(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.key())
                .remove(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.key())
                .remove(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.key());

        tag(Utils.blockTag("diagonalwindows", "non_diagonal_panes")).add(FBContent.BLOCK_FRAMED_BARS.key());
    }

    @SuppressWarnings("SameParameterValue")
    private ResourceKey<Block> key(String modId, String path) {
        return ResourceKey.create(registryKey, Utils.id(modId, path));
    }

    @Override
    protected BlockItemTagAppender<Block> tag(TagKey<Block> tag) {
        return new BlockItemTagAppender<>(super.tag(tag)) {
            @Override
            protected ResourceKey<Block> convertElement(BlockItemId element) {
                return element.block();
            }
        };
    }

    @Override
    public String getName() {
        return super.getName() + ": " + FramedConstants.MOD_ID;
    }
}
