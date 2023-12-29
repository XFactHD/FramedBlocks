package xfacthd.framedblocks;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.CrashReportCallables;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import xfacthd.framedblocks.common.config.CommonConfig;
import xfacthd.framedblocks.common.config.ServerConfig;
import xfacthd.framedblocks.common.data.capabilities.CapabilitySetup;
import xfacthd.framedblocks.common.data.cullupdate.CullingUpdatePacket;
import xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import xfacthd.framedblocks.common.data.shapes.ShapeReloader;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;
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
import xfacthd.framedblocks.common.net.*;
import xfacthd.framedblocks.common.util.*;

@Mod(FramedConstants.MOD_ID)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class FramedBlocks
{
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "3";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Utils.rl("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public FramedBlocks(IEventBus modBus)
    {
        FBContent.init(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

        modBus.addListener(CapabilitySetup::onRegisterCapabilities);
        modBus.addListener(FramedBlocks::onCommonSetup);
        modBus.addListener(FramedBlocks::onLoadComplete);

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

        CompatHandler.init();

        CrashReportCallables.registerCrashCallable(
                "FramedBlocks BlockEntity Warning",
                FramedBlocks::getBlockEntityWarning,
                () -> ServerConfig.allowBlockEntities
        );
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        CHANNEL.messageBuilder(SignUpdatePacket.class, 0, PlayNetworkDirection.PLAY_TO_SERVER)
                .encoder(SignUpdatePacket::encode)
                .decoder(SignUpdatePacket::decode)
                .consumerNetworkThread(SignUpdatePacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenSignScreenPacket.class, 1, PlayNetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenSignScreenPacket::encode)
                .decoder(OpenSignScreenPacket::new)
                .consumerNetworkThread(OpenSignScreenPacket::handle)
                .add();

        CHANNEL.messageBuilder(CullingUpdatePacket.class, 2, PlayNetworkDirection.PLAY_TO_CLIENT)
                .encoder(CullingUpdatePacket::encode)
                .decoder(CullingUpdatePacket::decode)
                .consumerNetworkThread(CullingUpdatePacket::handle)
                .add();

        CHANNEL.messageBuilder(SelectFramingSawRecipePacket.class, 3, PlayNetworkDirection.PLAY_TO_SERVER)
                .encoder(SelectFramingSawRecipePacket::encode)
                .decoder(SelectFramingSawRecipePacket::new)
                .consumerMainThread(SelectFramingSawRecipePacket::handle)
                .add();

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
