package xfacthd.framedblocks.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.datagen.providers.*;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
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
        gen.addProvider(new FramingSawRecipeProvider(gen));
        BlockTagsProvider tagProvider = new FramedBlockTagProvider(gen, fileHelper);
        gen.addProvider(tagProvider);
        gen.addProvider(new FramedItemTagProvider(gen, tagProvider, fileHelper));
        gen.addProvider(new FramedLanguageProvider(gen));
    }



    private GeneratorHandler() { }
}