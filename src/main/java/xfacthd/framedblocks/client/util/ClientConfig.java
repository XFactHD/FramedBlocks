package xfacthd.framedblocks.client.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;

public final class ClientConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ClientConfig INSTANCE;

    private static final String KEY_SHOW_GHOST_BLOCKS = "showGhostBlocks";
    private static final String KEY_FANCY_HITBOXES = "fancyHitboxes";
    private static final String KEY_DETAILED_CULLING = "detailedCulling";
    private static final String KEY_USE_DISCRETE_UV_STEPS = "discreteUVSteps";
    private static final String KEY_STATE_LOCK_SHOW_DETAILS = "stateLockShowDetails";
    private static final String KEY_TOGGLE_WATERLOG_SHOW_DETAILS = "toggleWaterlogShowDetails";
    private static final String KEY_TOGGLE_Y_SLOPE_SHOW_DETAILS = "toggleYSlopeShowDetails";
    private static final String KEY_REINFORCED_SHOW_DETAILS = "reinforcedShowDetails";
    private static final String KEY_PRISM_OFFSET_SHOW_DETAILS = "prismOffsetShowDetails";
    private static final String KEY_SPLIT_LINES_SHOW_DETAILS = "splitLineShowDetails";
    private static final String KEY_ONE_WAY_WINDOW_SHOW_DETAILS = "oneWayWindowShowDetails";

    public static final String TRANSLATION_SHOW_GHOST_BLOCKS = translate(KEY_SHOW_GHOST_BLOCKS);
    public static final String TRANSLATION_FANCY_HITBOXES = translate(KEY_FANCY_HITBOXES);
    public static final String TRANSLATION_DETAILED_CULLING = translate(KEY_DETAILED_CULLING);
    public static final String TRANSLATION_USE_DISCRETE_UV_STEPS = translate(KEY_USE_DISCRETE_UV_STEPS);
    public static final String TRANSLATION_STATE_LOCK_SHOW_DETAILS = translate(KEY_STATE_LOCK_SHOW_DETAILS);
    public static final String TRANSLATION_TOGGLE_WATERLOG_SHOW_DETAILS = translate(KEY_TOGGLE_WATERLOG_SHOW_DETAILS);
    public static final String TRANSLATION_TOGGLE_Y_SLOPE_SHOW_DETAILS = translate(KEY_TOGGLE_Y_SLOPE_SHOW_DETAILS);
    public static final String TRANSLATION_REINFORCED_SHOW_DETAILS = translate(KEY_REINFORCED_SHOW_DETAILS);
    public static final String TRANSLATION_PRISM_OFFSET_SHOW_DETAILS = translate(KEY_PRISM_OFFSET_SHOW_DETAILS);
    public static final String TRANSLATION_SPLIT_LINES_SHOW_DETAILS = translate(KEY_SPLIT_LINES_SHOW_DETAILS);
    public static final String TRANSLATION_ONE_WAY_WINDOW_SHOW_DETAILS = translate(KEY_ONE_WAY_WINDOW_SHOW_DETAILS);

    public static boolean showGhostBlocks = false;
    public static boolean fancyHitboxes = false;
    public static boolean detailedCulling = false;
    public static boolean useDiscreteUVSteps = false;
    public static boolean stateLockShowDetails;
    public static boolean toggleWaterlogShowDetails;
    public static boolean toggleYSlopeShowDetails;
    public static boolean reinforcedShowDetails;
    public static boolean prismOffsetShowDetails;
    public static boolean splitLineShowDetails;
    public static boolean oneWayWindowShowDetails;

    private final ForgeConfigSpec.BooleanValue showGhostBlocksValue;
    private final ForgeConfigSpec.BooleanValue fancyHitboxesValue;
    private final ForgeConfigSpec.BooleanValue detailedCullingValue;
    private final ForgeConfigSpec.BooleanValue useDiscreteUVStepsValue;

    private final ForgeConfigSpec.BooleanValue stateLockShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue toggleWaterlogShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue toggleYSlopeShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue reinforcedShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue prismOffsetShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue splitLineShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue oneWayWindowShowDetailsValue;

    static
    {
        final Pair<ClientConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    public ClientConfig(ForgeConfigSpec.Builder builder)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        builder.push("general");
        showGhostBlocksValue = builder
                .comment("Wether ghost blocks are shown when you are holding a framed block")
                .translation(TRANSLATION_SHOW_GHOST_BLOCKS)
                .define(KEY_SHOW_GHOST_BLOCKS, true);
        fancyHitboxesValue = builder
                .comment("Wether certain framed blocks should show fancy hitboxes")
                .translation(TRANSLATION_FANCY_HITBOXES)
                .define(KEY_FANCY_HITBOXES, true);
        detailedCullingValue = builder
                .comment("If false only full block faces of framed blocks will be culled, if true all outer faces of famed blocks can be culled")
                .translation(TRANSLATION_DETAILED_CULLING)
                .define(KEY_DETAILED_CULLING, true);
        useDiscreteUVStepsValue = builder
                .comment("If true, the UV remapping will use discrete steps to avoid floating point errors")
                .translation(TRANSLATION_USE_DISCRETE_UV_STEPS)
                .define(KEY_USE_DISCRETE_UV_STEPS, true);
        builder.pop();

        builder.push("overlay");
        stateLockShowDetailsValue = builder
                .comment("If true, the State Lock overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_STATE_LOCK_SHOW_DETAILS)
                .define(KEY_STATE_LOCK_SHOW_DETAILS, true);
        toggleWaterlogShowDetailsValue = builder
                .comment("If true, the Toggle Waterloggable overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_TOGGLE_WATERLOG_SHOW_DETAILS)
                .define(KEY_TOGGLE_WATERLOG_SHOW_DETAILS, true);
        toggleYSlopeShowDetailsValue = builder
                .comment("If true, the Toggle Slope Face overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_TOGGLE_Y_SLOPE_SHOW_DETAILS)
                .define(KEY_TOGGLE_Y_SLOPE_SHOW_DETAILS, true);
        reinforcedShowDetailsValue = builder
                .comment("If true, the Reinforcement overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_REINFORCED_SHOW_DETAILS)
                .define(KEY_REINFORCED_SHOW_DETAILS, true);
        prismOffsetShowDetailsValue = builder
                .comment("If true, the Prism Offset overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_PRISM_OFFSET_SHOW_DETAILS)
                .define(KEY_PRISM_OFFSET_SHOW_DETAILS, true);
        splitLineShowDetailsValue = builder
                .comment("If true, the Collapsible Block Split Line overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_SPLIT_LINES_SHOW_DETAILS)
                .define(KEY_SPLIT_LINES_SHOW_DETAILS, true);
        oneWayWindowShowDetailsValue = builder
                .comment("If true, the One-Way Window overlay will show detailed info, if false, it will only show an icon")
                .translation(TRANSLATION_ONE_WAY_WINDOW_SHOW_DETAILS)
                .define(KEY_ONE_WAY_WINDOW_SHOW_DETAILS, true);
        builder.pop();
    }

    private static String translate(String key)
    {
        return Utils.translateConfig("client", key);
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getModId().equals(FramedConstants.MOD_ID))
        {
            showGhostBlocks = showGhostBlocksValue.get();
            fancyHitboxes = fancyHitboxesValue.get();
            detailedCulling = detailedCullingValue.get();
            useDiscreteUVSteps = useDiscreteUVStepsValue.get();

            stateLockShowDetails = stateLockShowDetailsValue.get();
            toggleWaterlogShowDetails = toggleWaterlogShowDetailsValue.get();
            toggleYSlopeShowDetails = toggleYSlopeShowDetailsValue.get();
            reinforcedShowDetails = reinforcedShowDetailsValue.get();
            prismOffsetShowDetails = prismOffsetShowDetailsValue.get();
            splitLineShowDetails = splitLineShowDetailsValue.get();
            oneWayWindowShowDetails = oneWayWindowShowDetailsValue.get();
        }
    }
}