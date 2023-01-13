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
        public BlockLootTable() { super(Set.of(), FeatureFlags.VANILLA_SET); }

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
                    .filter(block -> block != FBContent.blockFramedDoor.get() &&
                            block != FBContent.blockFramedDoubleSlab.get() &&
                            block != FBContent.blockFramedDoublePanel.get() &&
                            block != FBContent.blockFramedIronDoor.get() &&
                            !(block instanceof FramedWaterloggablePressurePlateBlock) &&
                            !(block instanceof FramedWaterloggableWeightedPressurePlateBlock)
                    )
                    .forEach(this::dropSelf);

            dropOther(FBContent.blockFramedWaterloggablePressurePlate.get(), FBContent.blockFramedPressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableStonePressurePlate.get(), FBContent.blockFramedStonePressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableObsidianPressurePlate.get(), FBContent.blockFramedObsidianPressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableGoldPressurePlate.get(), FBContent.blockFramedGoldPressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableIronPressurePlate.get(), FBContent.blockFramedIronPressurePlate.get());

            add(FBContent.blockFramedDoor.get(), block -> createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
            add(FBContent.blockFramedDoubleSlab.get(), block -> droppingTwo(block, FBContent.blockFramedSlab.get()));
            add(FBContent.blockFramedDoublePanel.get(), block -> droppingTwo(block, FBContent.blockFramedPanel.get()));
            add(FBContent.blockFramedIronDoor.get(), block -> createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));

            dropOther(FBContent.blockFramedVerticalHalfSlope.get(), FBContent.blockFramedHalfSlope.get());
            dropOther(FBContent.blockFramedVerticalDoubleHalfSlope.get(), FBContent.blockFramedDoubleHalfSlope.get());
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