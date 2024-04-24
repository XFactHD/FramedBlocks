package xfacthd.framedblocks.common.datagen;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.datagen.providers.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(true, buildPackMetadata(output, event.getModContainer().getModInfo()));
        gen.addProvider(event.includeClient(), new FramedSpriteSourceProvider(output, lookupProvider, fileHelper));
        gen.addProvider(event.includeClient(), new FramedBlockStateProvider(output, fileHelper));
        gen.addProvider(event.includeClient(), new FramedItemModelProvider(output, fileHelper));
        gen.addProvider(event.includeServer(), new FramedLootTableProvider(output));
        gen.addProvider(event.includeServer(), new FramedRecipeProvider(output));
        gen.addProvider(event.includeServer(), new FramingSawRecipeProvider(output));
        BlockTagsProvider tagProvider = new FramedBlockTagProvider(output, lookupProvider, fileHelper);
        gen.addProvider(event.includeServer(), tagProvider);
        gen.addProvider(event.includeServer(), new FramedItemTagProvider(output, lookupProvider, tagProvider.contentsGetter(), fileHelper));
        gen.addProvider(event.includeClient(), new FramedLanguageProvider(output));
    }

    private static PackMetadataGenerator buildPackMetadata(PackOutput output, IModInfo modInfo)
    {
        return new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal(modInfo.getDisplayName() + " resources"),
                SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
                Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE))
        ));
    }



    private GeneratorHandler() { }
}