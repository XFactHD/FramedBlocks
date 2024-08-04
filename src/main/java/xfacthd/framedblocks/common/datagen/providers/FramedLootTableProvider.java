package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.datagen.loot.FramedBlockLootSubProvider;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class FramedLootTableProvider extends LootTableProvider
{
    public FramedLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerFuture)
    {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)
        ), providerFuture);
    }

    private static class BlockLootTable extends FramedBlockLootSubProvider
    {
        public BlockLootTable(HolderLookup.Provider lookupProvider)
        {
            super(lookupProvider);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return FBContent.getRegisteredBlocks()
                    .stream()
                    .map(Holder::value)
                    .collect(Collectors.toList());
        }

        @Override
        protected void generate()
        {
            dropOtherWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_PRESSURE_PLATE.value());
            dropOtherWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.value());
            dropOtherWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.value());
            dropOtherWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.value());
            dropOtherWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.value());

            dropDoorWithCamo(FBContent.BLOCK_FRAMED_DOOR.value());
            dropDoorWithCamo(FBContent.BLOCK_FRAMED_IRON_DOOR.value());
            dropMultipleWithCamo(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value(), FBContent.BLOCK_FRAMED_SLAB.value(), 2);
            dropMultipleWithCamo(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value(), FBContent.BLOCK_FRAMED_PANEL.value(), 2);

            dropOtherWithCamo(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value(), FBContent.BLOCK_FRAMED_HALF_SLOPE.value());
            dropOtherWithCamo(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.value(), FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.value());

            add(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT.value(), noDrop());

            FBContent.getRegisteredBlocks()
                    .stream()
                    .map(Holder::value)
                    .filter(IFramedBlock.class::isInstance)
                    .filter(block -> !map.containsKey(block.getLootTable()))
                    .forEach(this::dropSelfWithCamo);

            dropSelf(FBContent.BLOCK_FRAMING_SAW.value());
            dropSelf(FBContent.BLOCK_POWERED_FRAMING_SAW.value());
        }
    }
}
