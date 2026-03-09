package io.github.xfacthd.framedblocks.common.datagen;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import io.github.xfacthd.framedblocks.common.datagen.providers.*;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.Nullable;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class GeneratorHandler
{
    @Nullable
    public static DeferredItem<Item> framingSawPattern;

    public GeneratorHandler(IEventBus modBus)
    {
        if (DatagenModLoader.isRunningDataGen())
        {
            modBus.addListener(GeneratorHandler::onGatherData);

            DeferredRegister.Items items = DeferredRegister.createItems(FramedConstants.MOD_ID);
            items.register(modBus);
            framingSawPattern = items.registerSimpleItem(AppliedEnergisticsCompat.SAW_PATTERN_ID);
        }
    }

    private static void onGatherData(GatherDataEvent.Client event)
    {
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder()
                        .add(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY, FramedBlockOverlayProvider::buildBlockOverlayEntries)
        );

        event.createProvider(FramedSpriteSourceProvider::new);
        event.createProvider(FramedBlockModelProvider::new);
        event.createProvider(FramedItemModelProvider::new);
        event.createProvider(FramedLanguageProvider::new);

        event.createProvider(FramedLootTableProvider::new);
        event.createProvider(FramedRecipeProvider.Runner::new);
        event.createProvider(FramingSawRecipeProvider.Runner::new);
        event.createProvider(FramedBlockTagProvider::new);
        event.createProvider(FramedItemTagProvider::new);
        event.createProvider(FramedDataMapProvider::new);
    }
}
