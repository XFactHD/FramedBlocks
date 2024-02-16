package xfacthd.framedblocks.common.config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.SolidFrameMode;
import xfacthd.framedblocks.client.screen.overlay.BlockInteractOverlay;

public final class ClientConfig
{
    public static final ExtConfigView.Client VIEW = (ExtConfigView.Client) ConfigView.Client.INSTANCE;
    private static ModConfigSpec spec;

    private static final String KEY_SHOW_GHOST_BLOCKS = "showGhostBlocks";
    private static final String KEY_ALT_GHOST_RENDERER = "altGhostRenderer";
    private static final String KEY_FANCY_HITBOXES = "fancyHitboxes";
    private static final String KEY_DETAILED_CULLING = "detailedCulling";
    private static final String KEY_USE_DISCRETE_UV_STEPS = "discreteUVSteps";
    private static final String KEY_CON_TEX_MODE = "conTexMode";
    private static final String KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI = "showAllRecipePermutationsInEmi";
    private static final String KEY_SOLID_FRAME_MODE = "solidFrameMode";
    private static final String KEY_SHOW_BUTTON_PLATE_OVERLAY = "showButtonPlateTypeOverlay";
    private static final String KEY_SHOW_SPECIAL_CUBE_OVERLAY = "showSpecialCubeTypeOverlay";
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
    public static final String TRANSLATION_FANCY_HITBOXES = translate(KEY_FANCY_HITBOXES);
    public static final String TRANSLATION_DETAILED_CULLING = translate(KEY_DETAILED_CULLING);
    public static final String TRANSLATION_USE_DISCRETE_UV_STEPS = translate(KEY_USE_DISCRETE_UV_STEPS);
    public static final String TRANSLATION_CON_TEX_MODE = translate(KEY_CON_TEX_MODE);
    public static final String TRANSLATION_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI = translate(KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI);
    public static final String TRANSLATION_SOLID_FRAME_MODE = translate(KEY_SOLID_FRAME_MODE);
    public static final String TRANSLATION_SHOW_BUTTON_PLATE_OVERLAY = translate(KEY_SHOW_BUTTON_PLATE_OVERLAY);
    public static final String TRANSLATION_SHOW_SPECIAL_CUBE_OVERLAY = translate(KEY_SHOW_SPECIAL_CUBE_OVERLAY);
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
    private static boolean fancyHitboxes = false;
    private static boolean detailedCulling = false;
    private static boolean useDiscreteUVSteps = false;
    private static ConTexMode conTexMode = ConTexMode.DETAILED;
    private static boolean showAllRecipePermutationsInEmi = false;
    private static SolidFrameMode solidFrameMode = SolidFrameMode.DEFAULT;
    private static boolean showButtonPlateOverlay = false;
    private static boolean showSpecialCubeOverlay = false;
    private static BlockInteractOverlay.Mode stateLockMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode toggleWaterlogMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode toggleYSlopeMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode reinforcementMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode prismOffsetMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode splitLineMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode oneWayWindowMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode frameBackgroundMode = BlockInteractOverlay.Mode.DETAILED;
    private static BlockInteractOverlay.Mode camoRotationMode = BlockInteractOverlay.Mode.DETAILED;

    private static ModConfigSpec.BooleanValue showGhostBlocksValue;
    private static ModConfigSpec.BooleanValue altGhostRendererValue;
    private static ModConfigSpec.BooleanValue fancyHitboxesValue;
    private static ModConfigSpec.BooleanValue detailedCullingValue;
    private static ModConfigSpec.BooleanValue useDiscreteUVStepsValue;
    private static ModConfigSpec.EnumValue<ConTexMode> conTexModeValue;
    private static ModConfigSpec.BooleanValue showAllRecipePermutationsInEmiValue;
    private static ModConfigSpec.EnumValue<SolidFrameMode> solidFrameModeValue;
    private static ModConfigSpec.BooleanValue showButtonPlateOverlayValue;
    private static ModConfigSpec.BooleanValue showSpecialCubeOverlayValue;

    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> stateLockModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> toggleWaterlogModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> toggleYSlopeModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> reinforcementModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> prismOffsetModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> splitLineModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> oneWayWindowModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> frameBackgroundModeValue;
    private static ModConfigSpec.EnumValue<BlockInteractOverlay.Mode> camoRotationModeValue;

    public static ModConfigSpec create(IEventBus modBus)
    {
        modBus.addListener((ModConfigEvent.Loading event) -> onConfigReloaded(event));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfigReloaded(event));
        spec = new ModConfigSpec.Builder().configure(ClientConfig::build).getRight();
        return spec;
    }

    private static Object build(ModConfigSpec.Builder builder)
    {
        builder.push("general");
        showGhostBlocksValue = builder
                .comment("Whether ghost blocks are shown when you are holding a framed block")
                .translation(TRANSLATION_SHOW_GHOST_BLOCKS)
                .define(KEY_SHOW_GHOST_BLOCKS, true);
        altGhostRendererValue = builder
                .comment("If true, an alternate renderer will be used for the placement preview. May solve issues with certain shaders")
                .translation(TRANSLATION_ALT_GHOST_RENDERER)
                .define(KEY_ALT_GHOST_RENDERER, false);
        fancyHitboxesValue = builder
                .comment("Whether certain framed blocks should show fancy hitboxes")
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
        conTexModeValue = builder
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
        showAllRecipePermutationsInEmiValue = builder
                .comment("If true, all possible recipes of the Framing Saw will be added to EMI, else only the permutations using the Framed Cube will be added")
                .comment("This setting only has an effect when EMI is installed")
                .translation(TRANSLATION_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI)
                .define(KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI, true);
        solidFrameModeValue = builder
                .comment(
                        "Configures in which cases a framed block without a camo gets a solid model",
                        "If NEVER, the default frame texture will always be used",
                        "If DEFAULT, certain blocks will use the default frame texture with a solid background texture",
                        "If ALWAYS, all blocks will use the default frame texture with a solid background texture"
                )
                .translation(TRANSLATION_SOLID_FRAME_MODE)
                .defineEnum(KEY_SOLID_FRAME_MODE, SolidFrameMode.DEFAULT);
        showButtonPlateOverlayValue = builder
                .comment(
                        "If enabled, non-wooden buttons and pressure plates will show a material overlay when a camo is applied",
                        "Requires resource reload to take effect"
                )
                .translation(TRANSLATION_SHOW_BUTTON_PLATE_OVERLAY)
                .define(KEY_SHOW_BUTTON_PLATE_OVERLAY, true);
        showSpecialCubeOverlayValue = builder
                .comment(
                        "If enabled, special cube blocks will show a type overlay when a camo is applied",
                        "Requires resource reload to take effect"
                )
                .translation(TRANSLATION_SHOW_SPECIAL_CUBE_OVERLAY)
                .define(KEY_SHOW_SPECIAL_CUBE_OVERLAY, true);
        builder.pop();

        builder.push("overlay");
        stateLockModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("State Lock"))
                .comment(COMMENT_OVERLAY_ICON.formatted("State Lock"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("State Lock"))
                .translation(TRANSLATION_STATE_LOCK_MODE)
                .defineEnum(KEY_STATE_LOCK_MODE, BlockInteractOverlay.Mode.DETAILED);
        toggleWaterlogModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Toggle Waterloggable"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Toggle Waterloggable"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Toggle Waterloggable"))
                .translation(TRANSLATION_TOGGLE_WATERLOG_MODE)
                .defineEnum(KEY_TOGGLE_WATERLOG_MODE, BlockInteractOverlay.Mode.DETAILED);
        toggleYSlopeModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Toggle Slope Face"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Toggle Slope Face"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Toggle Slope Face"))
                .translation(TRANSLATION_TOGGLE_Y_SLOPE_MODE)
                .defineEnum(KEY_TOGGLE_Y_SLOPE_MODE, BlockInteractOverlay.Mode.DETAILED);
        reinforcementModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Reinforcement"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Reinforcement"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Reinforcement"))
                .translation(TRANSLATION_REINFORCEMENT_MODE)
                .defineEnum(KEY_REINFORCEMENT_MODE, BlockInteractOverlay.Mode.DETAILED);
        prismOffsetModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Prism Offset"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Prism Offset"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Prism Offset"))
                .translation(TRANSLATION_PRISM_OFFSET_MODE)
                .defineEnum(KEY_PRISM_OFFSET_MODE, BlockInteractOverlay.Mode.DETAILED);
        splitLineModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Collapsible Block Split Line"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Collapsible Block Split Line"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Collapsible Block Split Line"))
                .translation(TRANSLATION_SPLIT_LINES_MODE)
                .defineEnum(KEY_SPLIT_LINES_MODE, BlockInteractOverlay.Mode.DETAILED);
        oneWayWindowModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("One-Way Window"))
                .comment(COMMENT_OVERLAY_ICON.formatted("One-Way Window"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("One-Way Window"))
                .translation(TRANSLATION_ONE_WAY_WINDOW_MODE)
                .defineEnum(KEY_ONE_WAY_WINDOW_MODE, BlockInteractOverlay.Mode.DETAILED);
        frameBackgroundModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Item Frame Background"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Item Frame Background"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Item Frame Background"))
                .translation(TRANSLATION_FRAME_BACKGROUND_MODE)
                .defineEnum(KEY_FRAME_BACKGROUND_MODE, BlockInteractOverlay.Mode.DETAILED);
        camoRotationModeValue = builder
                .comment(COMMENT_OVERLAY_HIDDEN.formatted("Camo Rotation"))
                .comment(COMMENT_OVERLAY_ICON.formatted("Camo Rotation"))
                .comment(COMMENT_OVERLAY_DETAILED.formatted("Camo Rotation"))
                .translation(TRANSLATION_CAMO_ROTATION_MODE)
                .defineEnum(KEY_CAMO_ROTATION_MODE, BlockInteractOverlay.Mode.DETAILED);
        builder.pop();

        return null;
    }

    private static String translate(String key)
    {
        return Utils.translateConfig("client", key);
    }

    private static void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getSpec() == spec)
        {
            showGhostBlocks = showGhostBlocksValue.get();
            altGhostRenderer = altGhostRendererValue.get();
            fancyHitboxes = fancyHitboxesValue.get();
            detailedCulling = detailedCullingValue.get();
            useDiscreteUVSteps = useDiscreteUVStepsValue.get();
            conTexMode = conTexModeValue.get();
            showAllRecipePermutationsInEmi = showAllRecipePermutationsInEmiValue.get();
            solidFrameMode = solidFrameModeValue.get();
            showButtonPlateOverlay = showButtonPlateOverlayValue.get();
            showSpecialCubeOverlay = showSpecialCubeOverlayValue.get();

            stateLockMode = stateLockModeValue.get();
            toggleWaterlogMode = toggleWaterlogModeValue.get();
            toggleYSlopeMode = toggleYSlopeModeValue.get();
            reinforcementMode = reinforcementModeValue.get();
            prismOffsetMode = prismOffsetModeValue.get();
            splitLineMode = splitLineModeValue.get();
            oneWayWindowMode = oneWayWindowModeValue.get();
            frameBackgroundMode = frameBackgroundModeValue.get();
            camoRotationMode = camoRotationModeValue.get();
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
