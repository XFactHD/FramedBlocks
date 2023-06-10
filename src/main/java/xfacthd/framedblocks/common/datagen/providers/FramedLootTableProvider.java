package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.FramedWaterloggablePressurePlateBlock;
import xfacthd.framedblocks.common.block.interactive.FramedWaterloggableWeightedPressurePlateBlock;

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
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }

        @Override
        protected void generate()
        {
            FBContent.getRegisteredBlocks()
                    .stream()
                    .map(RegistryObject::get)
                    .filter(block -> block != FBContent.BLOCK_FRAMED_DOOR.get() &&
                            block != FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get() &&
                            block != FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get() &&
                            block != FBContent.BLOCK_FRAMED_IRON_DOOR.get() &&
                            !(block instanceof FramedWaterloggablePressurePlateBlock) &&
                            !(block instanceof FramedWaterloggableWeightedPressurePlateBlock)
                    )
                    .forEach(this::dropSelf);

            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.get(), FBContent.BLOCK_FRAMED_PRESSURE_PLATE.get());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.get(), FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.get());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.get(), FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.get());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.get(), FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.get());
            dropOther(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.get(), FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.get());

            add(FBContent.BLOCK_FRAMED_DOOR.get(), block -> createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
            add(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get(), block -> droppingTwo(block, FBContent.BLOCK_FRAMED_SLAB.get()));
            add(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get(), block -> droppingTwo(block, FBContent.BLOCK_FRAMED_PANEL.get()));
            add(FBContent.BLOCK_FRAMED_IRON_DOOR.get(), block -> createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));

            dropOther(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.get(), FBContent.BLOCK_FRAMED_HALF_SLOPE.get());
            dropOther(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get(), FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.get());
        }

        protected LootTable.Builder droppingTwo(Block block, Block drop) {
            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(
                            applyExplosionDecay(block, LootItem.lootTableItem(drop).apply(
                                    SetItemCountFunction.setCount(ConstantValue.exactly(2))
                                    )
                            )
                    )
            );
        }
    }
}