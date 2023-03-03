package xfacthd.framedblocks.client.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.api.util.FramedConstants;

public class ClientConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ClientConfig INSTANCE;

    public static boolean showGhostBlocks = false;
    public static boolean fancyHitboxes = false;
    public static boolean detailedCulling = false;
    public static boolean useDiscreteUVSteps = false;
    public static boolean stateLockShowDetails;
    public static boolean toggleWaterlogShowDetails;
    public static boolean toggleYSlopeShowDetails;
    public static boolean reinforcedShowDetails;

    private final ForgeConfigSpec.BooleanValue showGhostBlocksValue;
    private final ForgeConfigSpec.BooleanValue fancyHitboxesValue;
    private final ForgeConfigSpec.BooleanValue detailedCullingValue;
    private final ForgeConfigSpec.BooleanValue useDiscreteUVStepsValue;

    private final ForgeConfigSpec.BooleanValue stateLockShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue toggleWaterlogShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue toggleYSlopeShowDetailsValue;
    private final ForgeConfigSpec.BooleanValue reinforcedShowDetailsValue;

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
                .translation("config." + FramedConstants.MOD_ID + ".showGhostBlocks")
                .define("showGhostBlocks", true);
        fancyHitboxesValue = builder
                .comment("Wether certain framed blocks should show fancy hitboxes")
                .translation("config." + FramedConstants.MOD_ID + ".fancyHitboxes")
                .define("fancyHitboxes", true);
        detailedCullingValue = builder
                .comment("If false only full block faces of framed blocks will be culled, if true all outer faces of famed blocks can be culled")
                .translation("config." + FramedConstants.MOD_ID + ".detailedCulling")
                .define("detailedCulling", true);
        useDiscreteUVStepsValue = builder
                .comment("If true, the UV remapping will use discrete steps to avoid floating point errors")
                .translation("config." + FramedConstants.MOD_ID + ".discreteUVSteps")
                .define("discreteUVSteps", true);
        builder.pop();

        builder.push("overlay");
        stateLockShowDetailsValue = builder
                .comment("If true, the State Lock overlay will show detailed info, if false, it will only show an icon")
                .translation("config." + FramedConstants.MOD_ID + ".stateLockShowDetails")
                .define("stateLockShowDetails", true);
        toggleWaterlogShowDetailsValue = builder
                .comment("If true, the Toggle Waterloggable overlay will show detailed info, if false, it will only show an icon")
                .translation("config." + FramedConstants.MOD_ID + ".stateLockShowDetails")
                .define("toggleWaterlogShowDetails", true);
        toggleYSlopeShowDetailsValue = builder
                .comment("If true, the Toggle Slope Face overlay will show detailed info, if false, it will only show an icon")
                .translation("config." + FramedConstants.MOD_ID + ".stateLockShowDetails")
                .define("toggleYSlopeShowDetails", true);
        reinforcedShowDetailsValue = builder
                .comment("If true, the Reinforcement overlay will show detailed info, if false, it will only show an icon")
                .translation("config." + FramedConstants.MOD_ID + "stateLockShowDetails")
                .define("reinforcedShowDetails", true);
        builder.pop();
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
        }
    }
}