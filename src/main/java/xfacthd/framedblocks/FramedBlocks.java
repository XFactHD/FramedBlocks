package xfacthd.framedblocks;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.config.ServerConfig;
import xfacthd.framedblocks.common.data.capabilities.CapabilitySetup;
import xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import xfacthd.framedblocks.common.data.shapes.ShapeReloader;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.compat.CompatHandler;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.data.BlueprintBehaviours;
import xfacthd.framedblocks.common.data.StateCacheBuilder;
import xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import xfacthd.framedblocks.common.data.conpreds.ConnectionPredicates;
import xfacthd.framedblocks.common.data.facepreds.FullFacePredicates;
import xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.net.NetworkHandler;
import xfacthd.framedblocks.common.util.EventHandler;

@Mod(FramedConstants.MOD_ID)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class FramedBlocks
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public FramedBlocks(IEventBus modBus, ModContainer modContainer)
    {
        FBContent.init(modBus);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.create(modBus));
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.create(modBus));

        modBus.addListener(CapabilitySetup::onRegisterCapabilities);
        modBus.addListener(FramedBlocks::onCommonSetup);
        modBus.addListener(FramedBlocks::onLoadComplete);
        modBus.addListener(NetworkHandler::onRegisterPayloads);

        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(EventHandler::onBlockLeftClick);
        forgeBus.addListener(CullingUpdateTracker::onServerLevelTick);
        forgeBus.addListener(FramingSawRecipeCache::onAddReloadListener);

        if (!FMLEnvironment.production)
        {
            forgeBus.addListener(FramedBlocks::onAddDebugReloadListener);
        }

        FullFacePredicates.PREDICATES.initialize();
        SideSkipPredicates.PREDICATES.initialize();
        ConnectionPredicates.PREDICATES.initialize();

        CompatHandler.init(modBus);

        CrashReportCallables.registerCrashCallable(
                "FramedBlocks BlockEntity Warning",
                FramedBlocks::getBlockEntityWarning,
                ServerConfig.VIEW::allowBlockEntities
        );
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        StateCacheBuilder.ensureStateCachesInitialized();
        BlueprintBehaviours.register();
        CompatHandler.commonSetup();
        CamoContainerFactories.registerCamoFactories();
    }

    private static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        FramedBlueprintItem.lockRegistration();
    }

    private static void onAddDebugReloadListener(final AddReloadListenerEvent event)
    {
        event.addListener(ShapeReloader.INSTANCE);
        event.addListener(StateCacheBuilder.CacheReloader.INSTANCE);
    }

    private static String getBlockEntityWarning()
    {
        return """
               
               \t\tThe 'allowBlockEntities' setting in the framedblocks-server.toml config file is enabled.
               \t\tIf this crash happened in FramedBlocks code, please try the following solutions before reporting:
               \t\t- If you can identify the block that was used as a camo and resulted in the crash, add the block to the blacklist tag
               \t\t- If you can't identify the block or the crash wasn't fixed, make a backup of the world and disable the mentioned config setting
               \t\tIf the crash still happens, please report it on the FramedBlocks GitHub repository
               """;
    }
}
