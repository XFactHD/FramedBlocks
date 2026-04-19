package io.github.xfacthd.framedblocks;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedFlowerPotBlock;
import io.github.xfacthd.framedblocks.common.capability.CapabilitySetup;
import io.github.xfacthd.framedblocks.common.compat.CompatHandler;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.data.BlueprintBehaviours;
import io.github.xfacthd.framedblocks.common.data.DataMapsSetup;
import io.github.xfacthd.framedblocks.common.data.NullCullPredicates;
import io.github.xfacthd.framedblocks.common.data.StateCacheBuilder;
import io.github.xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import io.github.xfacthd.framedblocks.common.data.camo.block.rotator.BlockCamoRotators;
import io.github.xfacthd.framedblocks.common.data.conpreds.ConnectionPredicates;
import io.github.xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import io.github.xfacthd.framedblocks.common.data.dynreg.DynamicRegistrySetup;
import io.github.xfacthd.framedblocks.common.data.facepreds.FullFacePredicates;
import io.github.xfacthd.framedblocks.common.data.overlaypreds.BlockOverlayPredicates;
import io.github.xfacthd.framedblocks.common.data.shapes.ShapeReloader;
import io.github.xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import io.github.xfacthd.framedblocks.common.net.NetworkHandler;
import io.github.xfacthd.framedblocks.common.util.EventHandler;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.CrashReportCallables;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.poi.ExtendPoiTypesEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@Mod(FramedConstants.MOD_ID)
public final class FramedBlocks {
    public FramedBlocks(IEventBus modBus, ModContainer modContainer) {
        FBContent.init(modBus);

        ClientConfig.init(modBus, modContainer);
        ServerConfig.init(modBus, modContainer);
        DevToolsConfig.init(modBus, modContainer);

        modBus.addListener(CapabilitySetup::onRegisterCapabilities);
        modBus.addListener(FramedBlocks::onCommonSetup);
        modBus.addListener(NetworkHandler::onRegisterPayloads);
        modBus.addListener(BlueprintBehaviours::onRegisterBlueprintCopyBehaviours);
        modBus.addListener(DataMapsSetup::onRegisterDataMapTypes);
        modBus.addListener(FramedBlocks::onExtendPoiTypes);
        modBus.addListener(DynamicRegistrySetup::onCreateDatapackRegistries);

        NeoForge.EVENT_BUS.addListener(EventHandler::onBlockLeftClick);
        NeoForge.EVENT_BUS.addListener(EventHandler::onBlockRightClick);
        NeoForge.EVENT_BUS.addListener(EventHandler::onServerShutdown);
        NeoForge.EVENT_BUS.addListener(EventHandler::onTagsUpdated);
        NeoForge.EVENT_BUS.addListener(CullingUpdateTracker::onServerLevelTick);
        NeoForge.EVENT_BUS.addListener(CullingUpdateTracker::onServerShutdown);
        NeoForge.EVENT_BUS.addListener(FramingSawRecipeCache::onAddReloadListener);
        NeoForge.EVENT_BUS.addListener(FramingSawRecipeCache::onDataPackSync);
        NeoForge.EVENT_BUS.addListener(FramingSawRecipeCache::onRecipesReceived);
        NeoForge.EVENT_BUS.addListener(DataMapsSetup::onDataMapsUpdated);
        NeoForge.EVENT_BUS.addListener(BlockCamoRotators::onDefaultComponentsBound);

        if (!Utils.PRODUCTION) {
            NeoForge.EVENT_BUS.addListener(FramedBlocks::onAddDebugReloadListener);
        }

        FullFacePredicates.PREDICATES.initialize();
        SideSkipPredicates.PREDICATES.initialize();
        ConnectionPredicates.PREDICATES.initialize();
        BlockOverlayPredicates.PREDICATES.initialize();
        NullCullPredicates.PREDICATES.initialize();

        CompatHandler.init(modBus);

        CrashReportCallables.registerCrashCallable(
                "FramedBlocks BlockEntity Warning",
                FramedBlocks::getBlockEntityWarning,
                ServerConfig.VIEW::allowBlockEntities
        );
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        StateCacheBuilder.initializeStateCaches();
        FramedBlueprintItem.init();
        CompatHandler.commonSetup();
        CamoContainerFactories.registerCamoFactories();
        FramedFlowerPotBlock.initPotMapping();
    }

    private static void onAddDebugReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(ShapeReloader.LISTENER_ID, ShapeReloader.INSTANCE);
        event.addListener(StateCacheBuilder.CacheReloader.LISTENER_ID, StateCacheBuilder.CacheReloader.INSTANCE);
    }

    private static void onExtendPoiTypes(ExtendPoiTypesEvent event) {
        event.addBlockToPoi(PoiTypes.LIGHTNING_ROD, FBContent.BLOCK_FRAMED_LIGHTNING_ROD.value());
    }

    private static String getBlockEntityWarning() {
        return """
               
               \t\tThe 'allowBlockEntities' setting in the framedblocks-server.toml config file is enabled.
               \t\tIf this crash happened in FramedBlocks code, please try the following solutions before reporting:
               \t\t- If you can identify the block that was used as a camo and resulted in the crash, add the block to the blacklist tag
               \t\t- If you can't identify the block or the crash wasn't fixed, make a backup of the world and disable the mentioned config setting
               \t\tIf the crash still happens, please report it on the FramedBlocks GitHub repository
               """;
    }
}
