package xfacthd.framedblocks.common.datagen.providers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedWaterloggablePressurePlateBlock;
import xfacthd.framedblocks.common.block.FramedWaterloggableWeightedPressurePlateBlock;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class FramedLootTableProvider extends LootTableProvider
{
    public FramedLootTableProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedConstants.MOD_ID; }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) { /*NOOP*/ }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return Collections.singletonList(Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK));
    }

    private static class BlockLootTable extends BlockLoot
    {
        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return FBContent.getRegisteredBlocks()
                    .stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }

        @Override
        protected void addTables()
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
        }

        protected static LootTable.Builder droppingTwo(Block block, Block drop) {
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