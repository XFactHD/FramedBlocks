package xfacthd.framedblocks.common.config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.client.model.SolidFrameMode;
import xfacthd.framedblocks.client.screen.overlay.BlockInteractOverlay;

public final class ClientConfig
{
    public static final ExtConfigView.Client VIEW = (ExtConfigView.Client) ConfigView.Client.INSTANCE;
    private static final ModConfigSpec SPEC;

    private static final String KEY_SHOW_GHOST_BLOCKS = "showGhostBlocks";
    private static final String KEY_ALT_GHOST_RENDERER = "altGhostRenderer";
    private static final String KEY_GHOST_RENDER_OPACITY = "ghostRenderOpacity";
    private static final String KEY_FANCY_HITBOXES = "fancyHitboxes";
    private static final String KEY_DETAILED_CULLING = "detailedCulling";
    private static final String KEY_USE_DISCRETE_UV_STEPS = "discreteUVSteps";
    private static final String KEY_CON_TEX_MODE = "conTexMode";
    private static final String KEY_CAMO_MESSAGE_VERBOSITY = "camoMessageVerbosity";
    private static final String KEY_FORCE_AO_ON_GLOWING_BLOCKS = "forceAoOnGlowingBlocks";
    private static final String KEY_RENDER_ITEM_MODELS_WITH_CAMO = "renderItemModelsWithCamo";
    private static final String KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI = "showAllRecipePermutationsInEmi";
    private static final String KEY_SOLID_FRAME_MODE = "solidFrameMode";
    private static final String KEY_SHOW_BUTTON_PLATE_OVERLAY = "showButtonPlateTypeOverlay";
    private static final String KEY_SHOW_SPECIAL_CUBE_OVERLAY = "showSpecialCubeTypeOverlay";
    private static final String KEY_RENDER_CAMO_IN_JADE = "renderCamoInJade";
    private static final String KEY_STATE_LOCK_MODE = "stateLockMode";
    private static final String KEY_TOGGLE_WATERLOG_MODE = "toggleWaterlogMode";
    private static final String KEY_TOGGLE_Y_SLOPE_MODE = "toggleYSlopeMode";
    private static final String KEY_REINFORCEMENT_MODE = "reinforcedMode";
    private static final String KEY_PRISM_OFFSET_MODE = "prismOffsetMode";
    private static final String KEY_SPLIT_LINES_MODE = "splitLineMode";
    private static final String KEY_ONE_WAY_WINDOW_MODE = "oneWayWindowMode";
    private static final String KEY_FRAME_BACKGROUND_MODE = "itemFrameBackgroundMode";
    private static final String KEY_CAMO_ROTATION_MODE = "camoRotationMode";

    public static final String TRANSLATION_SHOW_GHOST_BLOCKS = translate(KEY_SHOW_GHOST_BLOCKS);
    public static final String TRANSLATION_ALT_GHOST_RENDERER = translate(KEY_ALT_GHOST_RENDERER);
    public static final String TRANSLATION_GHOST_RENDER_OPACITY = translate(KEY_GHOST_RENDER_OPACITY);
    public static final String TRANSLATION_FANCY_HITBOXES = translate(KEY_FANCY_HITBOXES);
    public static final String TRANSLATION_DETAILED_CULLING = translate(KEY_DETAILED_CULLING);
    public static final String TRANSLATION_USE_DISCRETE_UV_STEPS = translate(KEY_USE_DISCRETE_UV_STEPS);
    public static final String TRANSLATION_CON_TEX_MODE = translate(KEY_CON_TEX_MODE);
    public static final String TRANSLATION_CAMO_MESSAGE_VERBOSITY = translate(KEY_CAMO_MESSAGE_VERBOSITY);
    public static final String TRANSLATION_FORCE_AO_ON_GLOWING_BLOCKS = translate(KEY_FORCE_AO_ON_GLOWING_BLOCKS);
    public static final String TRANSLATION_RENDER_ITEM_MODELS_WITH_CAMO = translate(KEY_RENDER_ITEM_MODELS_WITH_CAMO);
    public static final String TRANSLATION_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI = translate(KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI);
    public static final String TRANSLATION_SOLID_FRAME_MODE = translate(KEY_SOLID_FRAME_MODE);
    public static final String TRANSLATION_SHOW_BUTTON_PLATE_OVERLAY = translate(KEY_SHOW_BUTTON_PLATE_OVERLAY);
    public static final String TRANSLATION_SHOW_SPECIAL_CUBE_OVERLAY = translate(KEY_SHOW_SPECIAL_CUBE_OVERLAY);
    public static final String TRANSLATION_RENDER_CAMO_IN_JADE = translate(KEY_RENDER_CAMO_IN_JADE);
    public static final String TRANSLATION_STATE_LOCK_MODE = translate(KEY_STATE_LOCK_MODE);
    public static final String TRANSLATION_TOGGLE_WATERLOG_MODE = translate(KEY_TOGGLE_WATERLOG_MODE);
    public static final String TRANSLATION_TOGGLE_Y_SLOPE_MODE = translate(KEY_TOGGLE_Y_SLOPE_MODE);
    public static final String TRANSLATION_REINFORCEMENT_MODE = translate(KEY_REINFORCEMENT_MODE);
    public static final String TRANSLATION_PRISM_OFFSET_MODE = translate(KEY_PRISM_OFFSET_MODE);
    public static final String TRANSLATION_SPLIT_LINES_MODE = translate(KEY_SPLIT_LINES_MODE);
    public static final String TRANSLATION_ONE_WAY_WINDOW_MODE = translate(KEY_ONE_WAY_WINDOW_MODE);
    public static final String TRANSLATION_FRAME_BACKGROUND_MODE = translate(KEY_FRAME_BACKGROUND_MODE);
    public static final String TRANSLATION_CAMO_ROTATION_MODE = translate(KEY_CAMO_ROTATION_MODE);

    private static final String COMMENT_OVERLAY_HIDDEN = "If set to HIDDEN, the %s overlay will be completely hidden";
    private static final String COMMENT_OVERLAY_ICON = "If set to ICON, the %s overlay will only show an icon";
    private static final String COMMENT_OVERLAY_DETAILED = "If set to DETAILED, the %s overlay will show detailed info";

    private static boolean showGhostBlocks = false;
    private static boolean altGhostRenderer = false;
    private static int ghostRenderOpacity = 0;
    private static boolean fancyHitboxes = false;
    private static boolean detailedCulling = false;
    private static boolean useDiscreteUVSteps = false;
    private static ConTexMode conTexMode = ConTexMode.DETAILED;
    private static CamoMessageVerbosity camoMessageVerbosity = CamoMessageVerbosity.DEFAULT;
    private static boolean forceAoOnGlowingBlocks = false;
    private static boolean renderItemModelsWithCamo = false;
    private static boolean showAllRecipePermutationsInEmi = false;
    private static SolidFrameMode solidFrameMode = SolidFrameMode.DEFAULT;
    private static boolean showButtonPlateOverlay = false;
    private static boolean showSpecialCubeOverlay = false;
    private static boolean renderCamoInJade = false;
    private static BlockInteractOverlay.Mode stateLockMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode toggleWaterlogMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode toggleYSlopeMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode reinforcementMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode prismOffsetMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode splitLineMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode oneWayWindowMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode frameBackgroundMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode camoRotationMode = BlockInteractOverlay.Mode.DETAILED;

    private static final ModConfigSpec.BooleanValue SHOW_GHOST_BLOCKS_VALUE;
    private static final ModConfigSpec.BooleanValue ALT_GHOST_RENDERER_VALUE;
    private static final ModConfigSpec.IntValue GHOST_RENDER_OPACITY_VALUE;
    private static final ModConfigSpec.BooleanValue FANCY_HITBOXES_VALUE;
    private static final ModConfigSpec.BooleanValue DETAILED_CULLING_VALUE;
    private static final ModConfigSpec.BooleanValue USE_DISCRETE_UV_STEPS_VALUE;
    private static final ModConfigSpec.EnumValue<ConTexMode> CON_TEX_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<CamoMessageVerbosity> CAMO_MESSAGE_VERBOSITY_VALUE;
    private static final ModConfigSpec.BooleanValue FORCE_AO_ON_GLOWING_BLOCKS_VALUE;
    private static final ModConfigSpec.BooleanValue RENDER_ITEM_MODELS_WITH_CAMO_VALUE;
    private static final ModConfigSpec.BooleanValue SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE;
    private static final ModConfigSpec.EnumValue<SolidFrameMode> SOLID_FRAME_MODE_VALUE;
    private static final ModConfigSpec.BooleanValue SHOW_BUTTON_PLATE_OVERLAY_VALUE;
    private static final ModConfigSpec.BooleanValue SHOW_SPECIAL_CUBE_OVERLAY_VALUE;
    private static final ModConfigSpec.BooleanValue RENDER_CAMO_IN_JADE_VALUE;

    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> STATE_LOCK_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> TOGGLE_WATERLOG_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> TOGGLE_Y_SLOPE_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> REINFORCEMENT_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> PRISM_OFFSET_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> SPLIT_LINE_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> ONE_WAY_WINDOW_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> FRAME_BACKGROUND_MODE_VALUE;
    private static final ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> CAMO_ROTATION_MODE_VALUE;

    public static void init(IEventBus modBus, ModContainer modContainer)
    {
        modBus.addListener((ModConfigEvent.Loading event) -> onConfigReloaded(event));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfigReloaded(event));
        modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("general");
        SHOW_GHOST_BLOCKS_VALUE = builder
                .comment("Whether ghost blocks are shown when you are holding a framed block")
                .translation(TRANSLATION_SHOW_GHOST_BLOCKS)
                .define(KEY_SHOW_GHOST_BLOCKS, true);
        ALT_GHOST_RENDERER_VALUE = builder
                .comment("If true, an alternate renderer will be used for the placement preview. May solve issues with certain shaders")
                .translation(TRANSLATION_ALT_GHOST_RENDERER)
                .define(KEY_ALT_GHOST_RENDERER, false);
        GHOST_RENDER_OPACITY_VALUE = builder
                .comment("Set the opacity of the placement preview. 30 is almost completely transparent, 255 is fully opaque")
                .translation(TRANSLATION_GHOST_RENDER_OPACITY)
                .defineInRange(KEY_GHOST_RENDER_OPACITY, 170, 30, 255);
        FANCY_HITBOXES_VALUE = builder
                .comment("Whether certain framed blocks should show fancy hitboxes")
                .translation(TRANSLATION_FANCY_HITBOXES)
                .define(KEY_FANCY_HITBOXES, true);
        DETAILED_CULLING_VALUE = builder
                .comment("If false only full block faces of framed blocks will be culled, if true all outer faces of framed blocks can be culled")
                .translation(TRANSLATION_DETAILED_CULLING)
                .worldRestart()
                .define(KEY_DETAILED_CULLING, true);
        USE_DISCRETE_UV_STEPS_VALUE = builder
                .comment("If true, the UV remapping will use discrete steps to avoid floating point errors")
                .translation(TRANSLATION_USE_DISCRETE_UV_STEPS)
                .define(KEY_USE_DISCRETE_UV_STEPS, true);
        CON_TEX_MODE_VALUE = builder
                .comment(
                        "Configures how detailed connected textures are supported.",
                        "Use anything above FULL_EDGE at your own risk (performance impact, unexpected behaviour)!",
                        "If NONE, all connected textures support is disabled",
                        "If FULL_FACE, connected textures are supported on full faces",
                        "If FULL_EDGE, connected textures are supported as above and on faces whose connecting edge covers the full block width",
                        "If DETAILED, connected textures are supported as above and on most faces when interacting with other framed blocks"
                )
                .translation(TRANSLATION_CON_TEX_MODE)
                .defineEnum(KEY_CON_TEX_MODE, ConTexMode.DETAILED);
        CAMO_MESSAGE_VERBOSITY_VALUE = builder
                .comment(
                        "Configures the verbosity of messages displayed when a block cannot be used as a camo",
                        "If NONE, no message will be shown",
                        "If DEFAULT, a message will be shown when the block has a BlockEntity and isn't explicitly allowed or the block is explicitly disallowed",
                        "If DETAILED, a message will be shown as above or when a block is non-solid and not explicitly allowed"
                )
                .translation(TRANSLATION_CAMO_MESSAGE_VERBOSITY)
                .defineEnum(KEY_CAMO_MESSAGE_VERBOSITY, CamoMessageVerbosity.DEFAULT);
        FORCE_AO_ON_GLOWING_BLOCKS_VALUE = builder
                .comment(
                        "If true, ambient occlusion is applied to framed blocks which glow from applied glowstone dust.",
                        "If false, the vanilla behavior of disabling AO for light-emitting blocks is used"
                )
                .translation(TRANSLATION_FORCE_AO_ON_GLOWING_BLOCKS)
                .define(KEY_FORCE_AO_ON_GLOWING_BLOCKS, true);
        RENDER_ITEM_MODELS_WITH_CAMO_VALUE = builder
                .comment(
                        "If true, item models will be rendered with their camo, if present.",
                        "If false, item models will always be rendered without camo"
                )
                .translation(TRANSLATION_RENDER_ITEM_MODELS_WITH_CAMO)
                .define(KEY_RENDER_ITEM_MODELS_WITH_CAMO, true);
        SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE = builder
                .comment("If true, all possible recipes of the Framing Saw will be added to EMI, else only the permutations using the Framed Cube will be added")
                .comment("This setting only has an effect when EMI is installed")
                .translation(TRANSLATION_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI)
                .worldRestart()
                .define(KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI, true);
        SOLID_FRAME_MODE_VALUE = builder
                .comment(
                        "Configures in which cases a framed block without a camo gets a solid model",
                        "If NEVER, the default frame texture will always be used",
                        "If DEFAULT, certain blocks will use the default frame texture with a solid background texture",
                        "If ALWAYS, all blocks will use the default frame texture with a solid background texture"
                )
                .translation(TRANSLATION_SOLID_FRAME_MODE)
                .defineEnum(KEY_SOLID_FRAME_MODE, SolidFrameMode.DEFAULT);
        SHOW_BUTTON_PLATE_OVERLAY_VALUE = builder
                .comment(
                        "If enabled, non-wooden buttons and pressure plates will show a material overlay when a camo is applied",
                        "Requires resource reload to take effect"
                )
                .translation(TRANSLATION_SHOW_BUTTON_PLATE_OVERLAY)
                .define(KEY_SHOW_BUTTON_PLATE_OVERLAY, true);
        SHOW_SPECIAL_CUBE_OVERLAY_VALUE = builder
                .comment(
                        "If enabled, special cube blocks will show a type overlay when a camo is applied",
                        "Requires resource reload to take effect"
                )
                .translation(TRANSLATION_SHOW_SPECIAL_CUBE_OVERLAY)
                .define(KEY_SHOW_SPECIAL_CUBE_OVERLAY, true);
        RENDER_CAMO_IN_JADE_VALUE = builder
                .comment("If true, framed blocks will be rendered with their camo in Jade, otherwise they will be rendered blank")
                .translation(TRANSLATION_RENDER_CAMO_IN_JADE)
                .define(KEY_RENDER_CAMO_IN_JADE, true);
        builder.pop();

        builder.push("overlay");
        STATE_LOCK_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("State Lock"))
                .comment(COMMENT_OVERLAY_ICON.formatted("State Lock"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("State Lock"))
                .translation(TRANSLATION_STATE_LOCK_MODE)
                .defineEnum(KEY_STATE_LOCK_MODE, BlockInteractOverlay.Mode.DETAILED);
        TOGGLE_WATERLOG_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Toggle Waterloggable"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Toggle Waterloggable"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Toggle Waterloggable"))
                .translation(TRANSLATION_TOGGLE_WATERLOG_MODE)
                .defineEnum(KEY_TOGGLE_WATERLOG_MODE, BlockInteractOverlay.Mode.DETAILED);
        TOGGLE_Y_SLOPE_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Toggle Slope Face"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Toggle Slope Face"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Toggle Slope Face"))
                .translation(TRANSLATION_TOGGLE_Y_SLOPE_MODE)
                .defineEnum(KEY_TOGGLE_Y_SLOPE_MODE, BlockInteractOverlay.Mode.DETAILED);
        REINFORCEMENT_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Reinforcement"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Reinforcement"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Reinforcement"))
                .translation(TRANSLATION_REINFORCEMENT_MODE)
                .defineEnum(KEY_REINFORCEMENT_MODE, BlockInteractOverlay.Mode.DETAILED);
        PRISM_OFFSET_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Prism Offset"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Prism Offset"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Prism Offset"))
                .translation(TRANSLATION_PRISM_OFFSET_MODE)
                .defineEnum(KEY_PRISM_OFFSET_MODE, BlockInteractOverlay.Mode.DETAILED);
        SPLIT_LINE_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Collapsible Block Split Line"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Collapsible Block Split Line"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Collapsible Block Split Line"))
                .translation(TRANSLATION_SPLIT_LINES_MODE)
                .defineEnum(KEY_SPLIT_LINES_MODE, BlockInteractOverlay.Mode.DETAILED);
        ONE_WAY_WINDOW_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("One-Way Window"))
                .comment(COMMENT_OVERLAY_ICON.formatted("One-Way Window"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("One-Way Window"))
                .translation(TRANSLATION_ONE_WAY_WINDOW_MODE)
                .defineEnum(KEY_ONE_WAY_WINDOW_MODE, BlockInteractOverlay.Mode.DETAILED);
        FRAME_BACKGROUND_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Item Frame Background"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Item Frame Background"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Item Frame Background"))
                .translation(TRANSLATION_FRAME_BACKGROUND_MODE)
                .defineEnum(KEY_FRAME_BACKGROUND_MODE, BlockInteractOverlay.Mode.DETAILED);
        CAMO_ROTATION_MODE_VALUE = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Camo Rotation"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Camo Rotation"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Camo Rotation"))
                .translation(TRANSLATION_CAMO_ROTATION_MODE)
                .defineEnum(KEY_CAMO_ROTATION_MODE, BlockInteractOverlay.Mode.DETAILED);
        builder.pop();

        SPEC = builder.build();
    }

    private static String translate(String key)
    {
        return Utils.translateConfig("client", key);
    }

    private static void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getSpec() == SPEC)
        {
            showGhostBlocks = SHOW_GHOST_BLOCKS_VALUE.get();
            altGhostRenderer = ALT_GHOST_RENDERER_VALUE.get();
            ghostRenderOpacity = GHOST_RENDER_OPACITY_VALUE.get();
            fancyHitboxes = FANCY_HITBOXES_VALUE.get();
            detailedCulling = DETAILED_CULLING_VALUE.get();
            useDiscreteUVSteps = USE_DISCRETE_UV_STEPS_VALUE.get();
            conTexMode = CON_TEX_MODE_VALUE.get();
            camoMessageVerbosity = CAMO_MESSAGE_VERBOSITY_VALUE.get();
            forceAoOnGlowingBlocks = FORCE_AO_ON_GLOWING_BLOCKS_VALUE.get();
            renderItemModelsWithCamo = RENDER_ITEM_MODELS_WITH_CAMO_VALUE.get();
            showAllRecipePermutationsInEmi = SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE.get();
            solidFrameMode = SOLID_FRAME_MODE_VALUE.get();
            showButtonPlateOverlay = SHOW_BUTTON_PLATE_OVERLAY_VALUE.get();
            showSpecialCubeOverlay = SHOW_SPECIAL_CUBE_OVERLAY_VALUE.get();
            renderCamoInJade = RENDER_CAMO_IN_JADE_VALUE.get();

            stateLockMode = STATE_LOCK_MODE_VALUE.get();
            toggleWaterlogMode = TOGGLE_WATERLOG_MODE_VALUE.get();
            toggleYSlopeMode = TOGGLE_Y_SLOPE_MODE_VALUE.get();
            reinforcementMode = REINFORCEMENT_MODE_VALUE.get();
            prismOffsetMode = PRISM_OFFSET_MODE_VALUE.get();
            splitLineMode = SPLIT_LINE_MODE_VALUE.get();
            oneWayWindowMode = ONE_WAY_WINDOW_MODE_VALUE.get();
            frameBackgroundMode = FRAME_BACKGROUND_MODE_VALUE.get();
            camoRotationMode = CAMO_ROTATION_MODE_VALUE.get();
        }
    }

    private ClientConfig() { }



    public static final class ViewImpl implements ExtConfigView.Client
    {
        @Override
        public boolean showGhostBlocks()
        {
            return showGhostBlocks;
        }

        @Override
        public boolean useAltGhostRenderer()
        {
            return altGhostRenderer;
        }

        @Override
        public int getGhostRenderOpacity()
        {
            return ghostRenderOpacity;
        }

        @Override
        public boolean useFancySelectionBoxes()
        {
            return fancyHitboxes;
        }

        @Override
        public boolean detailedCullingEnabled()
        {
            return detailedCulling;
        }

        @Override
        public boolean useDiscreteUVSteps()
        {
            return useDiscreteUVSteps;
        }

        @Override
        public ConTexMode getConTexMode()
        {
            return conTexMode;
        }

        @Override
        public CamoMessageVerbosity getCamoMessageVerbosity()
        {
            return camoMessageVerbosity;
        }

        @Override
        public boolean shouldForceAmbientOcclusionOnGlowingBlocks()
        {
            return forceAoOnGlowingBlocks;
        }

        @Override
        public boolean shouldRenderItemModelsWithCamo()
        {
            return renderItemModelsWithCamo;
        }

        @Override
        public boolean showAllRecipePermutationsInEmi()
        {
            return showAllRecipePermutationsInEmi;
        }

        @Override
        public SolidFrameMode getSolidFrameMode()
        {
            return solidFrameMode;
        }

        @Override
        public boolean showButtonPlateOverlay()
        {
            return showButtonPlateOverlay;
        }

        @Override
        public boolean showSpecialCubeOverlay()
        {
            return showSpecialCubeOverlay;
        }

        @Override
        public boolean shouldRenderCamoInJade()
        {
            return renderCamoInJade;
        }

        @Override
        public BlockInteractOverlay.Mode getStateLockMode()
        {
            return stateLockMode;
        }

        @Override
        public BlockInteractOverlay.Mode getToggleWaterlogMode()
        {
            return toggleWaterlogMode;
        }

        @Override
        public BlockInteractOverlay.Mode getToggleYSlopeMode()
        {
            return toggleYSlopeMode;
        }

        @Override
        public BlockInteractOverlay.Mode getReinforcementMode()
        {
            return reinforcementMode;
        }

        @Override
        public BlockInteractOverlay.Mode getPrismOffsetMode()
        {
            return prismOffsetMode;
        }

        @Override
        public BlockInteractOverlay.Mode getSplitLineMode()
        {
            return splitLineMode;
        }

        @Override
        public BlockInteractOverlay.Mode getOneWayWindowMode()
        {
            return oneWayWindowMode;
        }

        @Override
        public BlockInteractOverlay.Mode getFrameBackgroundMode()
        {
            return frameBackgroundMode;
        }

        @Override
        public BlockInteractOverlay.Mode getCamoRotationMode()
        {
            return camoRotationMode;
        }
    }
}
