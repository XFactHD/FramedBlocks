package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.datagen.loot.FramedBlockLootSubProvider;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.loot.BoardAdditionalItemCountNumberProvider;
import io.github.xfacthd.framedblocks.common.data.loot.LayeredCubeAdditionalItemCountNumberProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class FramedLootTableProvider extends LootTableProvider {
    public FramedLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerFuture) {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)
        ), providerFuture);
    }

    private static class BlockLootTable extends FramedBlockLootSubProvider {
        public BlockLootTable(HolderLookup.Provider lookupProvider) {
            super(lookupProvider);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return FBContent.getRegisteredBlocks()
                    .stream()
                    .map(Holder::value)
                    .collect(Collectors.toList());
        }

        @Override
        protected void generate() {
            dropWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_PRESSURE_PLATE.value());
            dropWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.value());
            dropWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.value());
            dropWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.value());
            dropWithCamo(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.value(), FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.value());

            dropDoorWithCamo(FBContent.BLOCK_FRAMED_DOOR.value());
            dropDoorWithCamo(FBContent.BLOCK_FRAMED_IRON_DOOR.value());
            dropMultipleWithCamo(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value(), FBContent.BLOCK_FRAMED_SLAB.value(), 2);
            dropMultipleWithCamo(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value(), FBContent.BLOCK_FRAMED_PANEL.value(), 2);

            dropWithCamo(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value(), FBContent.BLOCK_FRAMED_HALF_SLOPE.value());
            dropWithCamo(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.value(), FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.value());

            add(
                    FBContent.BLOCK_FRAMED_BOARD.value(),
                    LootTable.lootTable()
                            .withPool(createDropWithCamoPool(FBContent.BLOCK_FRAMED_BOARD.value()))
                            .withPool(applyExplosionCondition(
                                    FBContent.BLOCK_FRAMED_BOARD.value(),
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0F))
                                            .add(applyExplosionDecay(
                                                    FBContent.BLOCK_FRAMED_BOARD.value(),
                                                    LootItem.lootTableItem(FBContent.BLOCK_FRAMED_BOARD.value())
                                            ).apply(SetItemCountFunction.setCount(BoardAdditionalItemCountNumberProvider.INSTANCE)))
                            ))
                            .withPool(createDynamicDropPool(FBContent.BLOCK_FRAMED_BOARD.value()))
            );

            dropSelfWithCamo(FBContent.BLOCK_FRAMED_TANK.value(), builder -> builder.apply(
                    CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                            .include(FBContent.DC_TYPE_TANK_CONTENTS.value())
            ));

            add(
                    FBContent.BLOCK_FRAMED_LAYERED_CUBE.value(),
                    LootTable.lootTable()
                            .withPool(createDropWithCamoPool(FBContent.BLOCK_FRAMED_LAYERED_CUBE.value()))
                            .withPool(applyExplosionCondition(
                                    FBContent.BLOCK_FRAMED_LAYERED_CUBE.value(),
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0F))
                                            .add(applyExplosionDecay(
                                                    FBContent.BLOCK_FRAMED_LAYERED_CUBE.value(),
                                                    LootItem.lootTableItem(FBContent.BLOCK_FRAMED_LAYERED_CUBE.value())
                                            ).apply(SetItemCountFunction.setCount(LayeredCubeAdditionalItemCountNumberProvider.INSTANCE)))
                            ))
                            .withPool(createDynamicDropPool(FBContent.BLOCK_FRAMED_LAYERED_CUBE.value()))
            );

            add(FBContent.BLOCK_FRAMED_UPPER_PYRAMID_SLAB.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT.value(), noDrop());
            add(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT.value(), noDrop());

            FBContent.getRegisteredBlocks()
                    .stream()
                    .map(Holder::value)
                    .filter(IFramedBlock.class::isInstance)
                    .filter(block -> !block.getLootTable().map(map::containsKey).orElse(true))
                    .forEach(this::dropSelfWithCamo);

            dropSelf(FBContent.BLOCK_FRAMING_SAW.value());
            dropSelf(FBContent.BLOCK_POWERED_FRAMING_SAW.value());
        }
    }
}
