package xfacthd.framedblocks.common.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;

public final class ServerConfig
{
    public static final ExtConfigView.Server VIEW = (ExtConfigView.Server) ConfigView.Server.INSTANCE;
    private static ModConfigSpec spec;

    private static final String KEY_ALLOW_BLOCK_ENTITIES = "allowBlockEntities";
    private static final String KEY_ENABLE_INTANGIBILITY = "enableIntangibleFeature";
    private static final String KEY_INTANGIBLE_MARKER = "intangibleMarkerItem";
    private static final String KEY_ONE_WAY_WINDOW_OWNABLE = "oneWayWindowOwnable";
    private static final String KEY_CONSUME_CAMO_ITEM = "consumeCamoItem";
    private static final String KEY_GLOWSTONE_LIGHT_LEVEL = "glowstoneLightLevel";
    private static final String KEY_FIREPROOF_BLOCKS = "fireproofBlocks";
    private static final String KEY_POWERED_SAW_ENERGY_CAPACITY = "energyCapacity";
    private static final String KEY_POWERED_SAW_MAX_RECEIVE = "maxReceive";
    private static final String KEY_POWERED_SAW_CONSUMPTION = "consumption";
    private static final String KEY_POWERED_SAW_RECIPE_DURATION = "craftingDuration";

    public static final String TRANSLATION_ALLOW_BLOCK_ENTITIES = translate(KEY_ALLOW_BLOCK_ENTITIES);
    public static final String TRANSLATION_ENABLE_INTANGIBILITY = translate(KEY_ENABLE_INTANGIBILITY);
    public static final String TRANSLATION_INTANGIBLE_MARKER = translate(KEY_INTANGIBLE_MARKER);
    public static final String TRANSLATION_ONE_WAY_WINDOW_OWNABLE = translate(KEY_ONE_WAY_WINDOW_OWNABLE);
    public static final String TRANSLATION_CONSUME_CAMO_ITEM = translate(KEY_CONSUME_CAMO_ITEM);
    public static final String TRANSLATION_GLOWSTONE_LIGHT_LEVEL = translate(KEY_GLOWSTONE_LIGHT_LEVEL);
    public static final String TRANSLATION_FIREPROOF_BLOCKS = translate(KEY_FIREPROOF_BLOCKS);
    public static final String TRANSLATION_POWERED_SAW_ENERGY_CAPACITY = translate(KEY_POWERED_SAW_ENERGY_CAPACITY);
    public static final String TRANSLATION_POWERED_SAW_MAX_RECEIVE = translate(KEY_POWERED_SAW_MAX_RECEIVE);
    public static final String TRANSLATION_POWERED_SAW_CONSUMPTION = translate(KEY_POWERED_SAW_CONSUMPTION);
    public static final String TRANSLATION_POWERED_SAW_RECIPE_DURATION = translate(KEY_POWERED_SAW_RECIPE_DURATION);

    private static boolean allowBlockEntities = false;
    private static boolean enableIntangibleFeature = false;
    private static Item intangibleMarkerItem = Items.PHANTOM_MEMBRANE;
    private static boolean oneWayWindowOwnable = true;
    private static boolean consumeCamoItem = true;
    private static int glowstoneLightLevel = 15;
    private static boolean fireproofBlocks = false;

    private static int poweredSawEnergyCapacity = 0;
    private static int poweredSawMaxReceive = 0;
    private static int poweredSawConsumption = 0;
    private static int poweredSawRecipeDuration = 0;

    private static ModConfigSpec.BooleanValue allowBlockEntitiesValue;
    private static ModConfigSpec.BooleanValue enableIntangibleFeatureValue;
    private static ModConfigSpec.ConfigValue<String> intangibleMarkerItemValue;
    private static ModConfigSpec.BooleanValue oneWayWindowOwnableValue;
    private static ModConfigSpec.BooleanValue consumeCamoItemValue;
    private static ModConfigSpec.IntValue glowstoneLightLevelValue;
    private static ModConfigSpec.BooleanValue fireproofBlocksValue;

    private static ModConfigSpec.IntValue poweredSawEnergyCapacityValue;
    private static ModConfigSpec.IntValue poweredSawMaxReceiveValue;
    private static ModConfigSpec.IntValue poweredSawConsumptionValue;
    private static ModConfigSpec.IntValue poweredSawRecipeDurationValue;

    public static ModConfigSpec create(IEventBus modBus)
    {
        modBus.addListener((ModConfigEvent.Loading event) -> onConfigReloaded(event));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfigReloaded(event));
        spec = new ModConfigSpec.Builder().configure(ServerConfig::build).getRight();
        return spec;
    }

    private static Object build(ModConfigSpec.Builder builder)
    {
        builder.push("general");
        allowBlockEntitiesValue = builder
                .comment("Whether blocks with block entities can be placed in framed blocks")
                .translation(TRANSLATION_ALLOW_BLOCK_ENTITIES)
                .define(KEY_ALLOW_BLOCK_ENTITIES, false);
        enableIntangibleFeatureValue = builder
                .comment("Enables the intangbility feature. Disabling this also prevents moving through blocks that are already marked as intangible")
                .translation(TRANSLATION_ENABLE_INTANGIBILITY)
                .define(KEY_ENABLE_INTANGIBILITY, false);
        intangibleMarkerItemValue = builder
                .comment("The item to use for making Framed Blocks intangible. The value must be a valid item registry name")
                .translation(TRANSLATION_INTANGIBLE_MARKER)
                .define(KEY_INTANGIBLE_MARKER, BuiltInRegistries.ITEM.getKey(Items.PHANTOM_MEMBRANE).toString(), ServerConfig::validateItemName);
        oneWayWindowOwnableValue = builder
                .comment("If true, only the player who placed the Framed One-Way Window can modify the window direction")
                .translation(TRANSLATION_ONE_WAY_WINDOW_OWNABLE)
                .define(KEY_ONE_WAY_WINDOW_OWNABLE, true);
        consumeCamoItemValue = builder
                .comment("If true, applying a camo will consume the item and removing the camo will drop it again")
                .translation(TRANSLATION_CONSUME_CAMO_ITEM)
                .define(KEY_CONSUME_CAMO_ITEM, true);
        glowstoneLightLevelValue = builder
                .comment("The light level to emit when glowstone dust is applied to a framed block")
                .translation(TRANSLATION_GLOWSTONE_LIGHT_LEVEL)
                .defineInRange(KEY_GLOWSTONE_LIGHT_LEVEL, 15, 0, 15);
        fireproofBlocksValue = builder
                .comment("If true, framed blocks are completely fire proof")
                .translation(TRANSLATION_FIREPROOF_BLOCKS)
                .define(KEY_FIREPROOF_BLOCKS, false);
        builder.pop();

        builder.push("powered_framing_saw");
        poweredSawEnergyCapacityValue = builder
                .comment("The amount of power the Powered Framing Saw can store")
                .translation(TRANSLATION_POWERED_SAW_ENERGY_CAPACITY)
                .defineInRange(KEY_POWERED_SAW_ENERGY_CAPACITY, 5000, 100, Short.MAX_VALUE);
        poweredSawMaxReceiveValue = builder
                .comment("The amount of power the Powered Framing Saw can receive per tick")
                .translation(TRANSLATION_POWERED_SAW_MAX_RECEIVE)
                .defineInRange(KEY_POWERED_SAW_MAX_RECEIVE, 250, 10, Short.MAX_VALUE);
        poweredSawConsumptionValue = builder
                .comment("The amount of power the Powered Framing Saw consumes per tick while crafting")
                .translation(TRANSLATION_POWERED_SAW_CONSUMPTION)
                .defineInRange(KEY_POWERED_SAW_CONSUMPTION, 50, 1, Short.MAX_VALUE);
        poweredSawRecipeDurationValue = builder
                .comment("How many ticks the Powered Framing Saw takes per crafting operation")
                .translation(TRANSLATION_POWERED_SAW_RECIPE_DURATION)
                .defineInRange(KEY_POWERED_SAW_RECIPE_DURATION, 30, 5, 200);
        builder.pop();

        return null;
    }

    private static boolean validateItemName(Object obj)
    {
        if (obj instanceof String name)
        {
            ResourceLocation key = new ResourceLocation(name);
            if (BuiltInRegistries.ITEM.containsKey(key))
            {
                return BuiltInRegistries.ITEM.get(key) != Items.AIR;
            }
        }
        return false;
    }

    private static String translate(String key)
    {
        return Utils.translateConfig("server", key);
    }

    private static void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.SERVER && event.getConfig().getSpec() == spec)
        {
            allowBlockEntities = allowBlockEntitiesValue.get();
            enableIntangibleFeature = enableIntangibleFeatureValue.get();
            intangibleMarkerItem = BuiltInRegistries.ITEM.get(new ResourceLocation(intangibleMarkerItemValue.get()));
            oneWayWindowOwnable = oneWayWindowOwnableValue.get();
            consumeCamoItem = consumeCamoItemValue.get();
            glowstoneLightLevel = glowstoneLightLevelValue.get();
            fireproofBlocks = fireproofBlocksValue.get();

            poweredSawEnergyCapacity = poweredSawEnergyCapacityValue.get();
            poweredSawMaxReceive = poweredSawMaxReceiveValue.get();
            poweredSawConsumption = poweredSawConsumptionValue.get();
            poweredSawRecipeDuration = poweredSawRecipeDurationValue.get();
        }
    }

    private ServerConfig() { }



    public static final class ViewImpl implements ExtConfigView.Server
    {
        private static boolean overrideIntangibilityConfig = false;

        @Override
        public boolean allowBlockEntities()
        {
            return allowBlockEntities;
        }

        @Override
        public void setOverrideIntangibilityConfig(boolean override)
        {
            overrideIntangibilityConfig = override;
        }

        @Override
        public boolean enableIntangibility()
        {
            return overrideIntangibilityConfig || enableIntangibleFeature;
        }

        @Override
        public Item getIntangibilityMarkerItem()
        {
            return intangibleMarkerItem;
        }

        @Override
        public boolean isOneWayWindowOwnable()
        {
            return oneWayWindowOwnable;
        }

        @Override
        public boolean shouldConsumeCamoItem()
        {
            return consumeCamoItem;
        }

        @Override
        public int getGlowstoneLightLevel()
        {
            return glowstoneLightLevel;
        }

        @Override
        public boolean areBlocksFireproof()
        {
            return fireproofBlocks;
        }

        @Override
        public int getPoweredSawEnergyCapacity()
        {
            return poweredSawEnergyCapacity;
        }

        @Override
        public int getPoweredSawMaxInput()
        {
            return poweredSawMaxReceive;
        }

        @Override
        public int getPoweredSawConsumption()
        {
            return poweredSawConsumption;
        }

        @Override
        public int getPoweredSawCraftingDuration()
        {
            return poweredSawRecipeDuration;
        }
    }
}
