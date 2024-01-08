package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;
import java.util.stream.Collectors;

public final class FramedLootTableProvider extends LootTableProvider
{
    public FramedLootTableProvider(PackOutput output)
    {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)
        ));
    }

    private static class BlockLootTable extends BlockLootSubProvider
    {
        public BlockLootTable()
        {
            super(Set.of(), FeatureFlags.VANILLA_SET);
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
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_PRESSURE_PLATE.value());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.value());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.value());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.value());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.value());

            dropDoor(FBContent.BLOCK_FRAMED_DOOR.value());
            dropDoor(FBContent.BLOCK_FRAMED_IRON_DOOR.value());
            dropTwoOf(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value(), FBContent.BLOCK_FRAMED_SLAB.value());
            dropTwoOf(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value(), FBContent.BLOCK_FRAMED_PANEL.value());

            dropOther(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value(), FBContent.BLOCK_FRAMED_HALF_SLOPE.value());
            dropOther(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.value(), FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.value());

            add(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT.value(), noDrop());

            FBContent.getRegisteredBlocks()
                    .stream()
                    .map(Holder::value)
                    .filter(block -> !map.containsKey(block.getLootTable()))
                    .forEach(this::dropSelf);
        }

        protected void dropTwoOf(Block block, Block drop)
        {
            add(block, b -> LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(
                            applyExplosionDecay(block, LootItem.lootTableItem(drop).apply(
                                            SetItemCountFunction.setCount(ConstantValue.exactly(2))
                                    )
                            )
                    )
            ));
        }

        protected void dropDoor(Block block)
        {
            add(block, this::createDoorTable);
        }
    }
}