package xfacthd.framedblocks;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.compat.CompatHandler;
import xfacthd.framedblocks.common.data.BlueprintBehaviours;
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
            new ResourceLocation(FramedConstants.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
	
    public static final CreativeModeTab FRAMED_TAB = new FramedCreativeTab();

    public FramedBlocks()
    {
        FBContent.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        FramedBlocksAPI.INSTANCE.accept(new ApiImpl());

        CompatHandler.init();

        CrashReportCallables.registerCrashCallable("FramedBlocks BlockEntity Warning", FramedBlocks::getBlockEntityWarning);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event)
    {
        CHANNEL.messageBuilder(SignUpdatePacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SignUpdatePacket::encode)
                .decoder(SignUpdatePacket::new)
                .consumer(SignUpdatePacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenSignScreenPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenSignScreenPacket::encode)
                .decoder(OpenSignScreenPacket::new)
                .consumer(OpenSignScreenPacket::handle)
                .add();

        BlueprintBehaviours.register();
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
