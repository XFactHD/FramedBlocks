package xfacthd.framedblocks.common.datagen.providers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.registries.ForgeRegistries;
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
            //noinspection ConstantConditions
            return ForgeRegistries.BLOCKS.getValues()
                    .stream()
                    .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                    .collect(Collectors.toList());
        }

        @Override
        protected void addTables()
        {
            //noinspection ConstantConditions
            ForgeRegistries.BLOCKS.getValues()
                    .stream()
                    .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                    .forEach(this::registerDropSelfLootTable);
        }
    }
}