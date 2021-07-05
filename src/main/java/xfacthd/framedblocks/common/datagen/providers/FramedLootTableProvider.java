package xfacthd.framedblocks.common.datagen.providers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class FramedLootTableProvider extends LootTableProvider
{
    public FramedLootTableProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker tracker) { /*NOOP*/ }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables()
    {
        return Collections.singletonList(Pair.of(BlockLootTable::new, LootParameterSets.BLOCK));
    }

    private static class BlockLootTable extends BlockLootTables
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
                            block != FBContent.blockFramedGhostBlock.get()
                    )
                    .forEach(this::dropSelf);

            add(FBContent.blockFramedDoor.get(), block -> createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
            add(FBContent.blockFramedDoubleSlab.get(), block -> droppingTwo(block, FBContent.blockFramedSlab.get()));
            add(FBContent.blockFramedDoublePanel.get(), block -> droppingTwo(block, FBContent.blockFramedPanel.get()));
        }

        protected static LootTable.Builder droppingTwo(Block block, Block drop) {
            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(
                            applyExplosionDecay(block, ItemLootEntry.lootTableItem(drop).apply(
                                    SetCount.setCount(ConstantRange.exactly(2))
                                    )
                            )
                    )
            );
        }
    }
}