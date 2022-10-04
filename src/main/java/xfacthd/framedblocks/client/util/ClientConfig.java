package xfacthd.framedblocks.client.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.api.util.FramedConstants;

public final class ClientConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ClientConfig INSTANCE;

    public static boolean showGhostBlocks = false;
    public static boolean fancyHitboxes = false;
    public static boolean detailedCulling = false;
    public static boolean useDiscreteUVSteps = false;

    private final ForgeConfigSpec.BooleanValue showGhostBlocksValue;
    private final ForgeConfigSpec.BooleanValue fancyHitboxesValue;
    private final ForgeConfigSpec.BooleanValue detailedCullingValue;
    private final ForgeConfigSpec.BooleanValue useDiscreteUVStepsValue;

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
        }
    }
}