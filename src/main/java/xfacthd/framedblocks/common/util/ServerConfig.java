package xfacthd.framedblocks.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.FramedBlocks;

public class ServerConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ServerConfig INSTANCE;

    public static boolean allowBlockEntities;
    public static boolean enablePassthrough;
    public static Item passthroughItem;

    private final ForgeConfigSpec.BooleanValue allowBlockEntitiesValue;
    private final ForgeConfigSpec.BooleanValue enablePassthroughValue;
    private final ForgeConfigSpec.ConfigValue<String> passthroughItemValue;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    @SuppressWarnings("ConstantConditions")
    public ServerConfig(ForgeConfigSpec.Builder builder)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        builder.push("general");
        allowBlockEntitiesValue = builder
                .comment("Whether blocks with block entities can be placed in Framed Blocks")
                .translation("config." + FramedBlocks.MODID + ".allowBlockEntities")
                .define("allowBlockEntities", false);
        enablePassthroughValue = builder
                .comment("Enables the passthrough feature. Disabling this prevents passing through already marked blocks as well")
                .translation("config." + FramedBlocks.MODID + ".enablePassthrough")
                .define("enablePassthrough", false);
        passthroughItemValue = builder
                .comment("The item to use for making Framed Blocks passthrough. The value must be a valid item registry name")
                .translation("config." + FramedBlocks.MODID + ".passThroughItem")
                .define("passThroughItem", Items.PHANTOM_MEMBRANE.getRegistryName().toString(), ServerConfig::validateItemName);
        builder.pop();
    }

    private static boolean validateItemName(Object obj)
    {
        if (obj instanceof String name)
        {
            ResourceLocation key = new ResourceLocation(name);
            if (ForgeRegistries.ITEMS.containsKey(key))
            {
                return ForgeRegistries.ITEMS.getValue(key) != Items.AIR;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.SERVER && event.getConfig().getModId().equals(FramedBlocks.MODID))
        {
            allowBlockEntities = allowBlockEntitiesValue.get();
            enablePassthrough = enablePassthroughValue.get();
            passthroughItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(passthroughItemValue.get()));
        }
    }
}
