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
import xfacthd.framedblocks.api.util.FramedConstants;

public final class ServerConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ServerConfig INSTANCE;

    public static boolean allowBlockEntities;
    public static boolean enableIntangibleFeature;
    public static Item intangibleMarkerItem;

    private final ForgeConfigSpec.BooleanValue allowBlockEntitiesValue;
    private final ForgeConfigSpec.BooleanValue enableIntangibleFeatureValue;
    private final ForgeConfigSpec.ConfigValue<String> intangibleMarkerItemValue;

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
                .translation("config." + FramedConstants.MOD_ID + ".allowBlockEntities")
                .define("allowBlockEntities", false);
        enableIntangibleFeatureValue = builder
                .comment("Enables the intangbility feature. Disabling this also prevents moving through blocks that are already marked as intangible")
                .translation("config." + FramedConstants.MOD_ID + ".enableIntangibleFeature")
                .define("enableIntangibleFeature", false);
        intangibleMarkerItemValue = builder
                .comment("The item to use for making Framed Blocks intangible. The value must be a valid item registry name")
                .translation("config." + FramedConstants.MOD_ID + ".intangibleMarkerItem")
                .define("intangibleMarkerItem", ForgeRegistries.ITEMS.getKey(Items.PHANTOM_MEMBRANE).toString(), ServerConfig::validateItemName);
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
        if (event.getConfig().getType() == ModConfig.Type.SERVER && event.getConfig().getSpec() == SPEC)
        {
            allowBlockEntities = allowBlockEntitiesValue.get();
            enableIntangibleFeature = enableIntangibleFeatureValue.get();
            intangibleMarkerItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(intangibleMarkerItemValue.get()));
        }
    }
}
