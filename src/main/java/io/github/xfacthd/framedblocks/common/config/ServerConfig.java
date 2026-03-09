package io.github.xfacthd.framedblocks.common.config;

import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig
{
    public static final ExtConfigView.Server VIEW = (ExtConfigView.Server) ConfigView.Server.INSTANCE;
    private static final ModConfigSpec SPEC;

    private static final String KEY_ALLOW_BLOCK_ENTITIES = "allowBlockEntities";
    private static final String KEY_ENABLE_INTANGIBILITY = "enableIntangibleFeature";
    private static final String KEY_ONE_WAY_WINDOW_OWNABLE = "oneWayWindowOwnable";
    private static final String KEY_CONSUME_CAMO_ITEM = "consumeCamoItem";
    private static final String KEY_GLOWSTONE_LIGHT_LEVEL = "glowstoneLightLevel";
    private static final String KEY_FIREPROOF_BLOCKS = "fireproofBlocks";
    private static final String KEY_POWERED_SAW_ENERGY_CAPACITY = "energyCapacity";
    private static final String KEY_POWERED_SAW_MAX_RECEIVE = "maxReceive";
    private static final String KEY_POWERED_SAW_CONSUMPTION = "consumption";
    private static final String KEY_POWERED_SAW_RECIPE_DURATION = "craftingDuration";

    public static final String TRANSLATION_CATEGORY_GENERAL = translate("category.general");
    public static final String TRANSLATION_CATEGORY_POWERED_FRAMING_SAW = translate("category.powered_framing_saw");

    public static final ModConfigSpec.BooleanValue ALLOW_BLOCK_ENTITIES_VALUE;
    public static final ModConfigSpec.BooleanValue ENABLE_INTANGIBILITY_VALUE;
    public static final ModConfigSpec.BooleanValue ONE_WAY_WINDOW_OWNABLE_VALUE;
    public static final ModConfigSpec.BooleanValue CONSUME_CAMO_ITEM_VALUE;
    public static final ModConfigSpec.IntValue GLOWSTONE_LIGHT_LEVEL_VALUE;
    public static final ModConfigSpec.BooleanValue FIREPROOF_BLOCKS_VALUE;

    public static final ModConfigSpec.IntValue POWERED_SAW_ENERGY_CAPACITY_VALUE;
    public static final ModConfigSpec.IntValue POWERED_SAW_MAX_RECEIVE_VALUE;
    public static final ModConfigSpec.IntValue POWERED_SAW_CONSUMPTION_VALUE;
    public static final ModConfigSpec.IntValue POWERED_SAW_RECIPE_DURATION_VALUE;

    private static boolean allowBlockEntities = false;
    private static boolean enableIntangibility = false;
    private static boolean oneWayWindowOwnable = true;
    private static boolean consumeCamoItem = true;
    private static int glowstoneLightLevel = 15;
    private static boolean fireproofBlocks = false;

    private static int poweredSawEnergyCapacity = 0;
    private static int poweredSawMaxReceive = 0;
    private static int poweredSawConsumption = 0;
    private static int poweredSawRecipeDuration = 0;

    public static void init(IEventBus modBus, ModContainer modContainer)
    {
        modBus.addListener((ModConfigEvent.Loading event) -> onConfigReloaded(event));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfigReloaded(event));
        modContainer.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.translation(TRANSLATION_CATEGORY_GENERAL).push("general");
        ALLOW_BLOCK_ENTITIES_VALUE = builder
                .comment("Whether blocks with block entities can be placed in framed blocks")
                .translation(translate(KEY_ALLOW_BLOCK_ENTITIES))
                .define(KEY_ALLOW_BLOCK_ENTITIES, false);
        ENABLE_INTANGIBILITY_VALUE = builder
                .comment("Enables the intangbility feature. Disabling this also prevents moving through blocks that are already marked as intangible")
                .translation(translate(KEY_ENABLE_INTANGIBILITY))
                .define(KEY_ENABLE_INTANGIBILITY, false);
        ONE_WAY_WINDOW_OWNABLE_VALUE = builder
                .comment("If true, only the player who placed the Framed One-Way Window can modify the window direction")
                .translation(translate(KEY_ONE_WAY_WINDOW_OWNABLE))
                .define(KEY_ONE_WAY_WINDOW_OWNABLE, true);
        CONSUME_CAMO_ITEM_VALUE = builder
                .comment("If true, applying a camo will consume the item and removing the camo will drop it again")
                .translation(translate(KEY_CONSUME_CAMO_ITEM))
                .define(KEY_CONSUME_CAMO_ITEM, true);
        GLOWSTONE_LIGHT_LEVEL_VALUE = builder
                .comment("The light level to emit when glowstone dust is applied to a framed block")
                .translation(translate(KEY_GLOWSTONE_LIGHT_LEVEL))
                .defineInRange(KEY_GLOWSTONE_LIGHT_LEVEL, 15, 0, 15);
        FIREPROOF_BLOCKS_VALUE = builder
                .comment("If true, framed blocks are completely fire proof")
                .translation(translate(KEY_FIREPROOF_BLOCKS))
                .define(KEY_FIREPROOF_BLOCKS, false);
        builder.pop();

        builder.translation(TRANSLATION_CATEGORY_POWERED_FRAMING_SAW).push("powered_framing_saw");
        POWERED_SAW_ENERGY_CAPACITY_VALUE = builder
                .comment("The amount of power the Powered Framing Saw can store")
                .translation(translate(KEY_POWERED_SAW_ENERGY_CAPACITY))
                .worldRestart()
                .defineInRange(KEY_POWERED_SAW_ENERGY_CAPACITY, 5000, 100, Short.MAX_VALUE);
        POWERED_SAW_MAX_RECEIVE_VALUE = builder
                .comment("The amount of power the Powered Framing Saw can receive per tick")
                .translation(translate(KEY_POWERED_SAW_MAX_RECEIVE))
                .worldRestart()
                .defineInRange(KEY_POWERED_SAW_MAX_RECEIVE, 250, 10, Short.MAX_VALUE);
        POWERED_SAW_CONSUMPTION_VALUE = builder
                .comment("The amount of power the Powered Framing Saw consumes per tick while crafting")
                .translation(translate(KEY_POWERED_SAW_CONSUMPTION))
                .worldRestart()
                .defineInRange(KEY_POWERED_SAW_CONSUMPTION, 50, 1, Short.MAX_VALUE);
        POWERED_SAW_RECIPE_DURATION_VALUE = builder
                .comment("How many ticks the Powered Framing Saw takes per crafting operation")
                .translation(translate(KEY_POWERED_SAW_RECIPE_DURATION))
                .worldRestart()
                .defineInRange(KEY_POWERED_SAW_RECIPE_DURATION, 30, 5, 200);
        builder.pop();

        SPEC = builder.build();
    }

    private static String translate(String key)
    {
        return Utils.translateConfig("server", key);
    }

    private static void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.SERVER && event.getConfig().getSpec() == SPEC)
        {
            allowBlockEntities = ALLOW_BLOCK_ENTITIES_VALUE.get();
            enableIntangibility = ENABLE_INTANGIBILITY_VALUE.get();
            oneWayWindowOwnable = ONE_WAY_WINDOW_OWNABLE_VALUE.get();
            consumeCamoItem = CONSUME_CAMO_ITEM_VALUE.get();
            glowstoneLightLevel = GLOWSTONE_LIGHT_LEVEL_VALUE.get();
            fireproofBlocks = FIREPROOF_BLOCKS_VALUE.get();

            poweredSawEnergyCapacity = POWERED_SAW_ENERGY_CAPACITY_VALUE.get();
            poweredSawMaxReceive = POWERED_SAW_MAX_RECEIVE_VALUE.get();
            poweredSawConsumption = POWERED_SAW_CONSUMPTION_VALUE.get();
            poweredSawRecipeDuration = POWERED_SAW_RECIPE_DURATION_VALUE.get();
        }
    }

    private ServerConfig() { }



    public static final class ViewImpl implements ExtConfigView.Server
    {
        @Override
        public boolean allowBlockEntities()
        {
            return allowBlockEntities;
        }

        @Override
        public boolean enableIntangibility()
        {
            return enableIntangibility;
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
