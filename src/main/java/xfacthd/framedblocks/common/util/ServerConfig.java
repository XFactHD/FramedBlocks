package xfacthd.framedblocks.common.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.FramedBlocks;

public class ServerConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ServerConfig INSTANCE;

    public static boolean allowBlockEntities;

    private final ForgeConfigSpec.BooleanValue allowBlockEntitiesValue;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    public ServerConfig(ForgeConfigSpec.Builder builder)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        builder.push("general");
        allowBlockEntitiesValue = builder
                .comment("Wether blocks with block entities can be placed in Framed Blocks")
                .translation("config." + FramedBlocks.MODID + ".allowBlockEntities")
                .define("allowBlockEntities", false);
        builder.pop();
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.SERVER && event.getConfig().getModId().equals(FramedBlocks.MODID))
        {
            allowBlockEntities = allowBlockEntitiesValue.get();
        }
    }
}
