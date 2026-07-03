package io.github.xfacthd.framedblocks.api.datagen.loot;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.datagen.loot.objects.RetainCamoLootCondition;
import io.github.xfacthd.framedblocks.api.datagen.loot.objects.SplitCamoLootFunction;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;
import java.util.function.Consumer;

/// Base block loot provider implementation providing helpers for loot tables with support for dropping camos.
public abstract class FramedBlockLootSubProvider extends BlockLootSubProvider {
    protected FramedBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.VANILLA_SET, lookupProvider);
    }

    /// Generate a loot table dropping the given block with camo.
    ///
    /// @param block The block to generate the table for
    protected void dropSelfWithCamo(Block block) {
        dropWithCamo(block, block);
    }

    /// Generate a loot table dropping the given block with camo.
    ///
    /// @param block        The block to generate the table for
    /// @param itemModifier A consumer receiving the item-to-drop for further modification
    protected void dropSelfWithCamo(Block block, Consumer<LootPoolSingletonContainer.Builder<?>> itemModifier) {
        dropWithCamo(block, block, itemModifier);
    }

    /// Generate a loot table dropping the given block with camo as the item of the given other block.
    ///
    /// @param block The block to generate the table for
    /// @param drop  The block whose item to drop
    protected void dropOtherWithCamo(Block block, Block drop) {
        dropWithCamo(block, drop);
    }

    /// Generate a loot table dropping the given block with camo as multiple items of the given other block,
    /// splitting the list of camos from to block across the dropped items.
    ///
    /// @param block The block to generate the table for
    /// @param drop  The block whose item to drop
    /// @param count The amount of items to drop
    @SuppressWarnings("SameParameterValue")
    protected void dropMultipleWithCamo(Block block, Block drop, int count) {
        add(block, _ -> {
            LootTable.Builder table = LootTable.lootTable();
            for (int i = 0; i < count; i++) {
                int index = i;
                table.withPool(createDropWithCamoPool(block, drop, builder ->
                        builder.apply(SplitCamoLootFunction.split(index).when(RetainCamoLootCondition.BUILDER))
                ));
            }
            return table.withPool(createDynamicDropPool(block));
        });
    }

    /// Generate a loot table dropping the given door block with camo.
    ///
    /// @param block The block to generate the table for
    protected final void dropDoorWithCamo(Block block) {
        dropWithCamo(block, block, builder -> builder.when(
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder
                                .properties()
                                .hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                        )
        ));
    }

    /// Generate a loot table dropping the given block with camo as the item of the given other block.
    ///
    /// @param block The block to generate the table for
    /// @param drop  The block whose item to drop
    protected final void dropWithCamo(Block block, Block drop) {
        dropWithCamo(block, drop, _ -> {});
    }

    /// Generate a loot table dropping the given block with camo as the item of the given other block.
    ///
    /// @param block        The block to generate the table for
    /// @param drop         The block whose item to drop
    /// @param itemModifier A consumer receiving the item-to-drop for further modification
    protected final void dropWithCamo(Block block, Block drop, Consumer<LootPoolSingletonContainer.Builder<?>> itemModifier) {
        add(block, funcBlock -> LootTable.lootTable()
                .withPool(createDropWithCamoPool(funcBlock, drop, itemModifier))
                .withPool(createDynamicDropPool(block))
        );
    }

    /// Create a loot pool handling the drop of the framed block item, including "retained" camo
    /// when the camos are not dropped separately (not droppable or used a tool retaining them).
    ///
    /// @param block The block to generate the table for
    protected final LootPool.Builder createDropWithCamoPool(Block block) {
        return createDropWithCamoPool(block, block, _ -> {});
    }

    /// Create a loot pool handling the drop of the framed block item, including "retained" camo
    /// when the camos are not dropped separately (not droppable or used a tool retaining them).
    ///
    /// @param block        The block to generate the table for
    /// @param drop         The block whose item to drop
    /// @param itemModifier A consumer receiving the item-to-drop for further modification
    protected final LootPool.Builder createDropWithCamoPool(Block block, Block drop, Consumer<LootPoolSingletonContainer.Builder<?>> itemModifier) {
        LootPoolSingletonContainer.Builder<?> tableItem = LootItem.lootTableItem(drop)
                .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                        .include(FramedConstants.Objects.DC_TYPE_CAMO_LIST.value())
                        .when(RetainCamoLootCondition.BUILDER)
                );
        itemModifier.accept(tableItem);
        return applyExplosionCondition(block, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(applyExplosionDecay(block, tableItem))
        );
    }

    /// Create a loot pool handling the dynamic drops of the block when camos are dropped separately.
    ///
    /// @param block The block to generate the pool for
    protected final LootPool.Builder createDynamicDropPool(Block block) {
        return applyExplosionCondition(block, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(applyExplosionDecay(block, DynamicLoot.dynamicEntry(IFramedBlock.DYNAMIC_DROPS)))
        );
    }
}
