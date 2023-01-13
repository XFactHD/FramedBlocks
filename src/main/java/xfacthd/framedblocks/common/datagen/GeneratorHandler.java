package xfacthd.framedblocks.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.datagen.providers.*;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(event.includeClient(), new FramedBlockStateProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new FramedItemModelProvider(output, fileHelper));
        gen.addProvider(event.includeServer(), new FramedLootTableProvider(output));
        gen.addProvider(event.includeServer(), new FramedRecipeProvider(output));
        BlockTagsProvider tagProvider = new FramedBlockTagProvider(output, lookupProvider, fileHelper);
        gen.addProvider(event.includeServer(), tagProvider);
        gen.addProvider(event.includeServer(), new FramedItemTagProvider(output, lookupProvider, tagProvider, fileHelper));
        gen.addProvider(event.includeClient(), new FramedLanguageProvider(output));
    }



    private GeneratorHandler() { }
}