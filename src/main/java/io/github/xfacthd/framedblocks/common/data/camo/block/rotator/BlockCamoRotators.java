package io.github.xfacthd.framedblocks.common.data.camo.block.rotator;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.RegisterBlockCamoRotatorsEvent;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.SinglePropertyBlockCamoRotator;
import io.github.xfacthd.framedblocks.common.data.DataMapsSetup;
import io.github.xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.InfestedRotatedPillarBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public final class BlockCamoRotators
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Reference2ObjectMap<Block, BlockCamoRotator> ROTATORS = new Reference2ObjectOpenHashMap<>();
    private static final BlockCamoRotator AXIS = new SinglePropertyBlockCamoRotator(RotatedPillarBlock.AXIS);
    private static final BlockCamoRotator DIR = new SinglePropertyBlockCamoRotator(DirectionalBlock.FACING);
    private static final BlockCamoRotator HOR_DIR = new SinglePropertyBlockCamoRotator(HorizontalDirectionalBlock.FACING);
    private static final BlockCamoRotator REDSTONE_LAMP = new SinglePropertyBlockCamoRotator(RedstoneLampBlock.LIT);

    public static BlockCamoRotator get(Block block)
    {
        synchronized (ROTATORS)
        {
            return ROTATORS.getOrDefault(block, BlockCamoRotator.DEFAULT);
        }
    }

    public static void reload()
    {
        synchronized (ROTATORS)
        {
            reloadSync();
        }
    }

    private static void reloadSync()
    {
        ROTATORS.clear();

        Stopwatch stopwatch = Stopwatch.createStarted();
        BuiltInRegistries.BLOCK.forEach(block ->
        {
            ItemStack stack = block.asItem().getDefaultInstance();
            if (stack.isEmpty()) return;

            CamoContainerFactory<?> factory = CamoContainerFactories.findCamoFactory(stack);
            if (!(factory instanceof AbstractBlockCamoContainerFactory<?> blockFactory)) return;
            if (!blockFactory.isValidBlockInternal(block.defaultBlockState())) return;

            switch (block)
            {
                case RotatedPillarBlock ignored -> addIfPropPresent(block, RotatedPillarBlock.AXIS, AXIS);
                case InfestedRotatedPillarBlock ignored -> addIfPropPresent(block, RotatedPillarBlock.AXIS, AXIS);
                case DirectionalBlock ignored -> addIfPropPresent(block, DirectionalBlock.FACING, DIR);
                case HorizontalDirectionalBlock ignored -> addIfPropPresent(block, HorizontalDirectionalBlock.FACING, HOR_DIR);
                case RedstoneLampBlock ignored -> addIfPropPresent(block, RedstoneLampBlock.LIT, REDSTONE_LAMP);
                default -> {}
            }
        });
        int defaultCount = ROTATORS.size();

        MutableInt customCount = new MutableInt();
        NeoForge.EVENT_BUS.post(new RegisterBlockCamoRotatorsEvent((key, value) ->
        {
            ROTATORS.put(key, value);
            customCount.increment();
        }));

        MutableInt datapackCount = new MutableInt();
        BuiltInRegistries.BLOCK.getDataMap(DataMapsSetup.BLOCK_CAMO_ROTATORS).forEach((key, prototype) ->
        {
            Block block = BuiltInRegistries.BLOCK.getValueOrThrow(key);
            if (!prototype.isApplicableTo(block))
            {
                LOGGER.error("BlockCamoRotator with properties {} from datamap cannot be applied to {}, dropping!", prototype.properties(), block);
                return;
            }

            ROTATORS.put(block, prototype.build(block));
            datapackCount.increment();
        });
        stopwatch.stop();
        int totalCount = ROTATORS.size();

        LOGGER.debug("Collected {} camo rotators ({} default, {} custom, {} datamap) in {}", totalCount, defaultCount, customCount.intValue(), datapackCount.intValue(), stopwatch);
    }

    private static void addIfPropPresent(Block block, Property<?> property, BlockCamoRotator rotator)
    {
        if (block.defaultBlockState().hasProperty(property))
        {
            ROTATORS.put(block, rotator);
        }
    }



    private BlockCamoRotators() { }
}
