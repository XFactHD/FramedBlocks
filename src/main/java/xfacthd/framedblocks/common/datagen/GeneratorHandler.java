package xfacthd.framedblocks.common.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.datagen.providers.*;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        gen.addProvider(new FramedBlockStateProvider(gen, fileHelper));
        gen.addProvider(new FramedItemModelProvider(gen, fileHelper));
        gen.addProvider(new FramedLootTableProvider(gen));
        gen.addProvider(new FramedRecipeProvider(gen));
        BlockTagsProvider tagProvider = new FramedTagProvider(gen, fileHelper);
        gen.addProvider(tagProvider);
        gen.addProvider(new FramedItemTagProvider(gen, tagProvider, fileHelper));
        gen.addProvider(new FramedLanguageProvider(gen));
    }
}