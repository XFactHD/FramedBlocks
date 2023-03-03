package xfacthd.framedblocks.client.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.api.util.*;

public final class ClientConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ClientConfig INSTANCE;

    public static boolean showGhostBlocks = false;
    public static boolean fancyHitboxes = false;
    public static boolean detailedCulling = false;
    public static boolean useDiscreteUVSteps = false;
    public static ConTexMode conTexMode = ConTexMode.FULL_FACE;
    public static boolean stateLockShowDetails;
    public static boolean toggleWaterlogShowDetails;
    public static boolean toggleYSlopeShowDetails;
    public static boolean reinforcedShowDetails;
    public static boolean prismOffsetShowDetails;
    public static boolean splitLineShowDetails;

    private final ForgeConfigSpec.BooleanValue showGhostBlocksValue;
    private final ForgeConfigSpec.BooleanValue fancyHitboxesValue;
    private final ForgeConfigSpec.BooleanValue detailedCullingValue;
    private final ForgeConfigSpec.BooleanValue useDiscreteUVStepsValue;
    private final ForgeConfigSpec.EnumValue<ConTexMode> conTexModeValue;

    private final ForgeConfigSpec.BooleanValue stateLockShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue toggleWaterlogShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue toggleYSlopeShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue reinforcedShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue prismOffsetShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue splitLineShowDetailsValue;

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
                .translation(Utils.translationKey("config", "showGhostBlocks"))
                .define("showGhostBlocks", true);
        fancyHitboxesValue = builder
                .comment("Wether certain framed blocks should show fancy hitboxes")
                .translation(Utils.translationKey("config", "fancyHitboxes"))
                .define("fancyHitboxes", true);
        detailedCullingValue = builder
                .comment("If false only full block faces of framed blocks will be culled, if true all outer faces of famed blocks can be culled")
                .translation(Utils.translationKey("config", "detailedCulling"))
                .define("detailedCulling", true);
        useDiscreteUVStepsValue = builder
                .comment("If true, the UV remapping will use discrete steps to avoid floating point errors")
                .translation(Utils.translationKey("config", "discreteUVSteps"))
                .define("discreteUVSteps", true);
        conTexModeValue = builder
                .comment(
                        "Configures how detailed connected textures are supported.",
                        "Use anything above FULL_FACE at your own risk (performance impact, unexpected behaviour)!",
                        "If NONE, all connected textures support is disabled",
                        "If FULL_FACE, connected textures are supported on full faces",
                        "If FULL_CON_FACE, connected textures are supported as above and on faces whose connecting neighbor covers a full face",
                        "If DETAILED, connected textures are supported as above and on most faces when interacting with other framed blocks"
                )
                .translation(Utils.translationKey("config", "conTexMode"))
                .defineEnum("conTexMode", ConTexMode.FULL_FACE);
        builder.pop();

        builder.push("overlay");
        stateLockShowDetailsValue = builder
                .comment("If true, the State Lock overlay will show detailed info, if false, it will only show an icon")
                .translation(Utils.translationKey("config", "stateLockShowDetails"))
                .define("stateLockShowDetails", true);
        toggleWaterlogShowDetailsValue = builder
                .comment("If true, the Toggle Waterloggable overlay will show detailed info, if false, it will only show an icon")
                .translation(Utils.translationKey("config", "toggleWaterlogShowDetails"))
                .define("toggleWaterlogShowDetails", true);
        toggleYSlopeShowDetailsValue = builder
                .comment("If true, the Toggle Slope Face overlay will show detailed info, if false, it will only show an icon")
                .translation(Utils.translationKey("config", "toggleYSlopeShowDetails"))
                .define("toggleYSlopeShowDetails", true);
        reinforcedShowDetailsValue = builder
                .comment("If true, the Reinforcement overlay will show detailed info, if false, it will only show an icon")
                .translation(Utils.translationKey("config", "reinforcedShowDetails"))
                .define("reinforcedShowDetails", true);
        prismOffsetShowDetailsValue = builder
                .comment("If true, the Prism Offset overlay will show detailed info, if false, it will only show an icon")
                .translation(Utils.translationKey("config", "prismOffsetShowDetails"))
                .define("prismOffsetShowDetails", true);
        splitLineShowDetailsValue = builder
                .comment("If true, the Collapsible Block Split Line overlay will show detailed info, if false, it will only show an icon")
                .translation(Utils.translationKey("config", "splitLineShowDetails"))
                .define("splitLineShowDetails", true);
        builder.pop();
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getSpec() == SPEC)
        {
            showGhostBlocks = showGhostBlocksValue.get();
            fancyHitboxes = fancyHitboxesValue.get();
            detailedCulling = detailedCullingValue.get();
            useDiscreteUVSteps = useDiscreteUVStepsValue.get();
            conTexMode = conTexModeValue.get();

            stateLockShowDetails = stateLockShowDetailsValue.get();
            toggleWaterlogShowDetails = toggleWaterlogShowDetailsValue.get();
            toggleYSlopeShowDetails = toggleYSlopeShowDetailsValue.get();
            reinforcedShowDetails = reinforcedShowDetailsValue.get();
            prismOffsetShowDetails = prismOffsetShowDetailsValue.get();
            splitLineShowDetails = splitLineShowDetailsValue.get();
        }
    }
}