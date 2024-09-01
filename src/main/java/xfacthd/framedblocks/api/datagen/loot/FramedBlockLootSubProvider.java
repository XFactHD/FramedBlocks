package xfacthd.framedblocks.api.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.datagen.loot.objects.NonTrivialCamoLootCondition;
import xfacthd.framedblocks.api.datagen.loot.objects.SplitCamoLootFunction;

import java.util.Set;
import java.util.function.Consumer;

public abstract class FramedBlockLootSubProvider extends BlockLootSubProvider
{
    protected FramedBlockLootSubProvider(HolderLookup.Provider lookupProvider)
    {
        super(Set.of(), FeatureFlags.VANILLA_SET, lookupProvider);
    }

    protected void dropSelfWithCamo(Block block)
    {
        dropWithCamo(block, block);
    }

    protected void dropSelfWithCamo(Block block, Consumer<LootPoolSingletonContainer.Builder<?>> itemModifier)
    {
        dropWithCamo(block, block, itemModifier);
    }

    protected void dropOtherWithCamo(Block block, Block drop)
    {
        dropWithCamo(block, drop);
    }

    @SuppressWarnings("SameParameterValue")
    protected void dropMultipleWithCamo(Block block, Block drop, int count)
    {
        add(block, funcBlock ->
        {
            LootTable.Builder table = LootTable.lootTable();
            for (int i = 0; i < count; i++)
            {
                int index = i;
                table.withPool(createDropWithCamoPool(block, drop, builder ->
                        builder.apply(SplitCamoLootFunction.split(index).when(NonTrivialCamoLootCondition.BUILDER))
                ));
            }
            return table;
        });
    }

    protected final void dropDoorWithCamo(Block block)
    {
        dropWithCamo(block, block, builder -> builder.when(LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder
                        .properties()
                        .hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                )
        ));
    }

    protected final void dropWithCamo(Block block, Block drop)
    {
        dropWithCamo(block, drop, builder -> {});
    }

    protected final void dropWithCamo(Block block, Block drop, Consumer<LootPoolSingletonContainer.Builder<?>> itemModifier)
    {
        add(block, funcBlock -> LootTable.lootTable().withPool(createDropWithCamoPool(funcBlock, drop, itemModifier)));
    }

    protected final LootPool.Builder createDropWithCamoPool(Block block, Block drop, Consumer<LootPoolSingletonContainer.Builder<?>> itemModifier)
    {
        LootPoolSingletonContainer.Builder<?> tableItem = LootItem.lootTableItem(drop)
                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                        .include(FBContent.DC_TYPE_CAMO_LIST.value())
                        .when(NonTrivialCamoLootCondition.BUILDER)
                );
        itemModifier.accept(tableItem);
        return applyExplosionCondition(block, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(applyExplosionDecay(block, tableItem))
        );
    }
}
