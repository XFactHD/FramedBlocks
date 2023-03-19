package xfacthd.framedblocks;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.update.CullingUpdatePacket;
import xfacthd.framedblocks.api.block.update.CullingUpdateTracker;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.compat.CompatHandler;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.data.BlueprintBehaviours;
import xfacthd.framedblocks.common.data.camo.CamoFactories;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.net.OpenSignScreenPacket;
import xfacthd.framedblocks.common.net.SignUpdatePacket;
import xfacthd.framedblocks.common.util.*;

@Mod(FramedConstants.MOD_ID)
@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FramedBlocks
{
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Utils.rl("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
	
    public static final CreativeModeTab FRAMED_TAB = new FramedCreativeTab();

    public FramedBlocks()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        FBContent.init(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        FramedBlocksAPI.INSTANCE.accept(new ApiImpl());

        modBus.addListener(FramedBlocks::onCommonSetup);
        modBus.addListener(FramedBlocks::onLoadComplete);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventHandler::onBlockLeftClick);
        forgeBus.addListener(CullingUpdateTracker::onServerLevelTick);
        forgeBus.addListener(FramingSawRecipeCache::onAddReloadListener);

        CompatHandler.init();

        CrashReportCallables.registerCrashCallable("FramedBlocks BlockEntity Warning", FramedBlocks::getBlockEntityWarning);
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        CHANNEL.messageBuilder(SignUpdatePacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SignUpdatePacket::encode)
                .decoder(SignUpdatePacket::decode)
                .consumerNetworkThread(SignUpdatePacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenSignScreenPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenSignScreenPacket::encode)
                .decoder(OpenSignScreenPacket::new)
                .consumerNetworkThread(OpenSignScreenPacket::handle)
                .add();

        CHANNEL.messageBuilder(CullingUpdatePacket.class, 2, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CullingUpdatePacket::encode)
                .decoder(CullingUpdatePacket::decode)
                .consumerNetworkThread(CullingUpdatePacket::handle)
                .add();

        BlueprintBehaviours.register();
        AbstractFramedDoubleBlock.cacheStatePairs();
        CompatHandler.commonSetup();
    }

    private static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        CamoFactories.lock();
        FramedBlueprintItem.lockRegistration();
    }

    private static String getBlockEntityWarning()
    {
        if (!ServerConfig.allowBlockEntities)
        {
            return "Not applicable";
        }
        else
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
}
