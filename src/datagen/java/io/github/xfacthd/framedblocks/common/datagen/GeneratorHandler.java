package io.github.xfacthd.framedblocks.common.datagen;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.datagen.dummy.DummyObjects;
import io.github.xfacthd.framedblocks.common.datagen.providers.*;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
public final class GeneratorHandler {
    public GeneratorHandler(IEventBus modBus) {
        if (DatagenModLoader.isRunningDataGen()) {
            DummyObjects.init(modBus);
            modBus.addListener(GeneratorHandler::onGatherData);
        }
    }

    private static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder()
                        .add(FramedConstants.Registries.BLOCK_OVERLAY_REGISTRY_KEY, FramedBlockOverlayProvider::buildBlockOverlayEntries)
        );

        event.createProvider(FramedSpriteSourceProvider::new);
        event.createProvider(FramedBlockModelProvider::new);
        event.createProvider(FramedItemModelProvider::new);
        event.createProvider(FramedLanguageProvider::new);
        event.createProvider(FramedTemplateProvider::new);

        event.createProvider(FramedLootTableProvider::new);
        event.createProvider(FramedRecipeProvider.Runner::new);
        event.createProvider(FramingSawRecipeProvider.Runner::new);
        event.createProvider(FramedBlockTagProvider::new);
        event.createProvider(FramedItemTagProvider::new);
        event.createProvider(FramedDataMapProvider::new);
        event.createProvider(FramedOverlayTagProvider::new);
    }
}
