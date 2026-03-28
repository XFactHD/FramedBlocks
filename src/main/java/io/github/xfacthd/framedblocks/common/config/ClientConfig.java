package io.github.xfacthd.framedblocks.common.config;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.screen.overlay.OverlayDisplayMode;
import io.github.xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.SolidFrameMode;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final ExtConfigView.Client VIEW = (ExtConfigView.Client) ConfigView.Client.INSTANCE;
    private static final ModConfigSpec SPEC;

    private static final String KEY_SHOW_GHOST_BLOCKS = "showGhostBlocks";
    private static final String KEY_ALT_GHOST_RENDERER = "altGhostRenderer";
    private static final String KEY_GHOST_RENDER_OPACITY = "ghostRenderOpacity";
    private static final String KEY_FANCY_HITBOXES = "fancyHitboxes";
    private static final String KEY_DETAILED_CULLING = "detailedCulling";
    private static final String KEY_CON_TEX_MODE = "conTexMode";
    private static final String KEY_CAMO_MESSAGE_VERBOSITY = "camoMessageVerbosity";
    private static final String KEY_FORCE_AO_ON_GLOWING_BLOCKS = "forceAoOnGlowingBlocks";
    private static final String KEY_RENDER_ITEM_MODELS_WITH_CAMO = "renderItemModelsWithCamo";
    private static final String KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI = "showAllRecipePermutationsInEmi";
    private static final String KEY_SOLID_FRAME_MODE = "solidFrameMode";
    private static final String KEY_SHOW_BUTTON_PLATE_OVERLAY = "showButtonPlateTypeOverlay";
    private static final String KEY_SHOW_SPECIAL_CUBE_OVERLAY = "showSpecialCubeTypeOverlay";
    private static final String KEY_RENDER_CAMO_IN_JADE = "renderCamoInJade";
    private static final String KEY_SHOW_CAMO_CRAFTING_IN_JEI = "showCamoCraftingInJei";
    private static final String KEY_MAX_OVERLAY_MODE = "maxOverlayMode";
    private static final String KEY_STATE_LOCK_MODE = "stateLockMode";
    private static final String KEY_TOGGLE_WATERLOG_MODE = "toggleWaterlogMode";
    private static final String KEY_TOGGLE_ALT_SLOPE_MODE = "toggleAltSlopeMode";
    private static final String KEY_REINFORCEMENT_MODE = "reinforcedMode";
    private static final String KEY_PRISM_OFFSET_MODE = "prismOffsetMode";
    private static final String KEY_SPLIT_LINES_MODE = "splitLineMode";
    private static final String KEY_ONE_WAY_WINDOW_MODE = "oneWayWindowMode";
    private static final String KEY_FRAME_BACKGROUND_MODE = "itemFrameBackgroundMode";
    private static final String KEY_CAMO_ROTATION_MODE = "camoRotationMode";
    private static final String KEY_TRAPDOOR_TEXTURE_ROTATION_MODE = "trapdoorTextureRotationMode";
    private static final String KEY_COPYCAT_STYLE_MODE = "copycatStyleMode";

    public static final String TRANSLATION_CATEGORY_GENERAL = translate("category.general");
    public static final String TRANSLATION_CATEGORY_OVERLAY = translate("category.overlay");

    private static final String COMMENT_OVERLAY_MAIN = "Controls the visibility of the %s overlay";
    private static final String COMMENT_OVERLAY_HIDDEN = "- If set to HIDDEN, the %s overlay will be completely hidden.";
    private static final String COMMENT_OVERLAY_ICON = "- If set to ICON, the %s overlay will only show an icon.";
    private static final String COMMENT_OVERLAY_DETAILED_TOGGLE = "- If set to DETAILED_TOGGLE, the %s overlay will show detailed info while crouching.";
    private static final String COMMENT_OVERLAY_DETAILED_ALWAYS = "- If set to DETAILED_ALWAYS, the %s overlay will always show detailed info.";

    public static final ModConfigSpec.BooleanValue SHOW_GHOST_BLOCKS_VALUE;
    public static final ModConfigSpec.BooleanValue ALT_GHOST_RENDERER_VALUE;
    public static final ModConfigSpec.IntValue GHOST_RENDER_OPACITY_VALUE;
    public static final ModConfigSpec.BooleanValue FANCY_HITBOXES_VALUE;
    public static final ModConfigSpec.BooleanValue DETAILED_CULLING_VALUE;
    public static final ModConfigSpec.EnumValue<ConTexMode> CON_TEX_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<CamoMessageVerbosity> CAMO_MESSAGE_VERBOSITY_VALUE;
    public static final ModConfigSpec.BooleanValue FORCE_AO_ON_GLOWING_BLOCKS_VALUE;
    public static final ModConfigSpec.BooleanValue RENDER_ITEM_MODELS_WITH_CAMO_VALUE;
    public static final ModConfigSpec.BooleanValue SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE;
    public static final ModConfigSpec.EnumValue<SolidFrameMode> SOLID_FRAME_MODE_VALUE;
    public static final ModConfigSpec.BooleanValue SHOW_BUTTON_PLATE_OVERLAY_VALUE;
    public static final ModConfigSpec.BooleanValue SHOW_SPECIAL_CUBE_OVERLAY_VALUE;
    public static final ModConfigSpec.BooleanValue RENDER_CAMO_IN_JADE_VALUE;
    public static final ModConfigSpec.BooleanValue SHOW_CAMO_CRAFTING_IN_JEI_VALUE;

    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> MAX_OVERLAY_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> STATE_LOCK_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> TOGGLE_WATERLOG_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> TOGGLE_ALT_SLOPE_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> REINFORCEMENT_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> PRISM_OFFSET_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> SPLIT_LINE_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> ONE_WAY_WINDOW_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> FRAME_BACKGROUND_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> CAMO_ROTATION_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> TRAPDOOR_TEXTURE_ROTATION_MODE_VALUE;
    public static final ModConfigSpec.EnumValue<OverlayDisplayMode> COPYCAT_STYLE_MODE_VALUE;

    private static boolean showGhostBlocks = false;
    private static boolean altGhostRenderer = false;
    private static int ghostRenderOpacity = 0;
    private static boolean fancyHitboxes = false;
    private static boolean detailedCulling = false;
    private static ConTexMode conTexMode = ConTexMode.DETAILED;
    private static CamoMessageVerbosity camoMessageVerbosity = CamoMessageVerbosity.DEFAULT;
    private static boolean forceAoOnGlowingBlocks = false;
    private static boolean renderItemModelsWithCamo = false;
    private static boolean showAllRecipePermutationsInEmi = false;
    private static SolidFrameMode solidFrameMode = SolidFrameMode.DEFAULT;
    private static boolean showButtonPlateOverlay = false;
    private static boolean showSpecialCubeOverlay = false;
    private static boolean renderCamoInJade = false;
    private static boolean showCamoCraftingInJei = false;

    private static OverlayDisplayMode maxOverlayMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode stateLockMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode toggleWaterlogMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode toggleAltSlopeMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode reinforcementMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode prismOffsetMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode splitLineMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode oneWayWindowMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode frameBackgroundMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode camoRotationMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode trapdoorTextureRotationMode = OverlayDisplayMode.DETAILED_ALWAYS;
    private static OverlayDisplayMode copycatStyleMode = OverlayDisplayMode.DETAILED_ALWAYS;

    public static void init(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener((ModConfigEvent.Loading event) -> onConfigReloaded(event));
        modBus.addListener((ModConfigEvent.Reloading event) -> onConfigReloaded(event));
        modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.translation(TRANSLATION_CATEGORY_GENERAL).push("general");
        SHOW_GHOST_BLOCKS_VALUE = builder
                .comment("Whether ghost blocks are shown when you are holding a framed block")
                .translation(translate(KEY_SHOW_GHOST_BLOCKS))
                .define(KEY_SHOW_GHOST_BLOCKS, true);
        ALT_GHOST_RENDERER_VALUE = builder
                .comment("If true, an alternate renderer will be used for the placement preview. May solve issues with certain shaders")
                .translation(translate(KEY_ALT_GHOST_RENDERER))
                .define(KEY_ALT_GHOST_RENDERER, false);
        GHOST_RENDER_OPACITY_VALUE = builder
                .comment("Set the opacity of the placement preview. 30 is almost completely transparent, 255 is fully opaque")
                .translation(translate(KEY_GHOST_RENDER_OPACITY))
                .defineInRange(KEY_GHOST_RENDER_OPACITY, 170, 30, 255);
        FANCY_HITBOXES_VALUE = builder
                .comment("Whether certain framed blocks should show fancy hitboxes")
                .translation(translate(KEY_FANCY_HITBOXES))
                .define(KEY_FANCY_HITBOXES, true);
        DETAILED_CULLING_VALUE = builder
                .comment("If false only full block faces of framed blocks will be culled, if true all outer faces of framed blocks can be culled")
                .translation(translate(KEY_DETAILED_CULLING))
                .worldRestart()
                .define(KEY_DETAILED_CULLING, true);
        CON_TEX_MODE_VALUE = builder
                .comment(
                        "Configures how detailed connected textures are supported.",
                        "- If NONE, all connected textures support is disabled",
                        "- If FULL_FACE, connected textures are supported on full faces",
                        "- If FULL_EDGE, connected textures are supported as above and on faces whose connecting edge covers the full block width",
                        "- If DETAILED, connected textures are supported as above and on most faces when interacting with other framed blocks"
                )
                .translation(translate(KEY_CON_TEX_MODE))
                .defineEnum(KEY_CON_TEX_MODE, ConTexMode.DETAILED);
        CAMO_MESSAGE_VERBOSITY_VALUE = builder
                .comment(
                        "Configures the verbosity of messages displayed when a block cannot be used as a camo",
                        "- If NONE, no message will be shown",
                        "- If DEFAULT, a message will be shown when the block has a BlockEntity and isn't explicitly allowed or the block is explicitly disallowed",
                        "- If DETAILED, a message will be shown as above or when a block is non-solid and not explicitly allowed"
                )
                .translation(translate(KEY_CAMO_MESSAGE_VERBOSITY))
                .defineEnum(KEY_CAMO_MESSAGE_VERBOSITY, CamoMessageVerbosity.DEFAULT);
        FORCE_AO_ON_GLOWING_BLOCKS_VALUE = builder
                .comment(
                        "If true, ambient occlusion is applied to framed blocks which glow from applied glowstone dust.",
                        "If false, the vanilla behavior of disabling AO for light-emitting blocks is used."
                )
                .translation(translate(KEY_FORCE_AO_ON_GLOWING_BLOCKS))
                .define(KEY_FORCE_AO_ON_GLOWING_BLOCKS, true);
        RENDER_ITEM_MODELS_WITH_CAMO_VALUE = builder
                .comment(
                        "If true, item models will be rendered with their camo, if present.",
                        "If false, item models will always be rendered without camo."
                )
                .translation(translate(KEY_RENDER_ITEM_MODELS_WITH_CAMO))
                .define(KEY_RENDER_ITEM_MODELS_WITH_CAMO, true);
        SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE = builder
                .comment("If true, all possible recipes of the Framing Saw will be added to EMI, else only the permutations using the Framed Cube will be added.")
                .comment("This setting only has an effect when EMI is installed.")
                .translation(translate(KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI))
                .worldRestart()
                .define(KEY_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI, true);
        SOLID_FRAME_MODE_VALUE = builder
                .comment(
                        "Configures in which cases a framed block without a camo gets a solid model",
                        "- If NEVER, the default frame texture will always be used",
                        "- If DEFAULT, certain blocks will use the default frame texture with a solid background texture",
                        "- If ALWAYS, all blocks will use the default frame texture with a solid background texture"
                )
                .translation(translate(KEY_SOLID_FRAME_MODE))
                .defineEnum(KEY_SOLID_FRAME_MODE, SolidFrameMode.DEFAULT);
        SHOW_BUTTON_PLATE_OVERLAY_VALUE = builder
                .comment(
                        "If enabled, non-wooden buttons and pressure plates will show a material overlay when a camo is applied.",
                        "Requires resource reload to take effect."
                )
                .translation(translate(KEY_SHOW_BUTTON_PLATE_OVERLAY))
                .define(KEY_SHOW_BUTTON_PLATE_OVERLAY, true);
        SHOW_SPECIAL_CUBE_OVERLAY_VALUE = builder
                .comment(
                        "If enabled, special cube blocks will show a type overlay when a camo is applied.",
                        "Requires resource reload to take effect."
                )
                .translation(translate(KEY_SHOW_SPECIAL_CUBE_OVERLAY))
                .define(KEY_SHOW_SPECIAL_CUBE_OVERLAY, true);
        RENDER_CAMO_IN_JADE_VALUE = builder
                .comment("If true, framed blocks will be rendered with their camo in Jade, otherwise they will be rendered blank.")
                .translation(translate(KEY_RENDER_CAMO_IN_JADE))
                .define(KEY_RENDER_CAMO_IN_JADE, true);
        SHOW_CAMO_CRAFTING_IN_JEI_VALUE = builder
                .comment("If true, camo application recipes will be shown in JEI")
                .translation(translate(KEY_SHOW_CAMO_CRAFTING_IN_JEI))
                .worldRestart()
                .define(KEY_SHOW_CAMO_CRAFTING_IN_JEI, true);
        builder.pop();

        builder.translation(TRANSLATION_CATEGORY_OVERLAY).push("overlay");
        MAX_OVERLAY_MODE_VALUE = builder
                .comment(
                        "Controls the maximum visibility of all overlays",
                        "- If set to HIDDEN, the overlays will be completely hidden.",
                        "- If set to ICON, the overlays will only show an icon.",
                        "- If set to DETAILED_TOGGLE, the overlays show detailed info while crouching.",
                        "- If set to DETAILED_ALWAYS, the overlays always show detailed info."
                )
                .translation(translate(KEY_MAX_OVERLAY_MODE))
                .defineEnum(KEY_MAX_OVERLAY_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        STATE_LOCK_MODE_VALUE = builder
                .comment(formatOverlayComments("State Lock"))
                .translation(translate(KEY_STATE_LOCK_MODE))
                .defineEnum(KEY_STATE_LOCK_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        TOGGLE_WATERLOG_MODE_VALUE = builder
                .comment(formatOverlayComments("Toggle Waterloggable"))
                .translation(translate(KEY_TOGGLE_WATERLOG_MODE))
                .defineEnum(KEY_TOGGLE_WATERLOG_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        TOGGLE_ALT_SLOPE_MODE_VALUE = builder
                .comment(formatOverlayComments("Toggle Slope Face"))
                .translation(translate(KEY_TOGGLE_ALT_SLOPE_MODE))
                .defineEnum(KEY_TOGGLE_ALT_SLOPE_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        REINFORCEMENT_MODE_VALUE = builder
                .comment(formatOverlayComments("Reinforcement"))
                .translation(translate(KEY_REINFORCEMENT_MODE))
                .defineEnum(KEY_REINFORCEMENT_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        PRISM_OFFSET_MODE_VALUE = builder
                .comment(formatOverlayComments("Prism Offset"))
                .translation(translate(KEY_PRISM_OFFSET_MODE))
                .defineEnum(KEY_PRISM_OFFSET_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        SPLIT_LINE_MODE_VALUE = builder
                .comment(formatOverlayComments("Collapsible Block Split Line"))
                .translation(translate(KEY_SPLIT_LINES_MODE))
                .defineEnum(KEY_SPLIT_LINES_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        ONE_WAY_WINDOW_MODE_VALUE = builder
                .comment(formatOverlayComments("One-Way Window"))
                .translation(translate(KEY_ONE_WAY_WINDOW_MODE))
                .defineEnum(KEY_ONE_WAY_WINDOW_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        FRAME_BACKGROUND_MODE_VALUE = builder
                .comment(formatOverlayComments("Item Frame Background"))
                .translation(translate(KEY_FRAME_BACKGROUND_MODE))
                .defineEnum(KEY_FRAME_BACKGROUND_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        CAMO_ROTATION_MODE_VALUE = builder
                .comment(formatOverlayComments("Camo Rotation"))
                .translation(translate(KEY_CAMO_ROTATION_MODE))
                .defineEnum(KEY_CAMO_ROTATION_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        TRAPDOOR_TEXTURE_ROTATION_MODE_VALUE = builder
                .comment(formatOverlayComments("Trapdoor Texture Rotation"))
                .translation(translate(KEY_TRAPDOOR_TEXTURE_ROTATION_MODE))
                .defineEnum(KEY_TRAPDOOR_TEXTURE_ROTATION_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        COPYCAT_STYLE_MODE_VALUE = builder
                .comment(formatOverlayComments("Copycat Style"))
                .translation(translate(KEY_COPYCAT_STYLE_MODE))
                .defineEnum(KEY_COPYCAT_STYLE_MODE, OverlayDisplayMode.DETAILED_ALWAYS);
        builder.pop();

        SPEC = builder.build();
    }

    private static String translate(String key) {
        return Utils.translateConfig("client", key);
    }

    private static String[] formatOverlayComments(String overlay) {
        return new String[] {
                COMMENT_OVERLAY_MAIN.formatted(overlay),
                COMMENT_OVERLAY_HIDDEN.formatted(overlay),
                COMMENT_OVERLAY_ICON.formatted(overlay),
                COMMENT_OVERLAY_DETAILED_TOGGLE.formatted(overlay),
                COMMENT_OVERLAY_DETAILED_ALWAYS.formatted(overlay)
        };
    }

    private static void onConfigReloaded(ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getSpec() == SPEC) {
            showGhostBlocks = SHOW_GHOST_BLOCKS_VALUE.get();
            altGhostRenderer = ALT_GHOST_RENDERER_VALUE.get();
            ghostRenderOpacity = GHOST_RENDER_OPACITY_VALUE.get();
            fancyHitboxes = FANCY_HITBOXES_VALUE.get();
            detailedCulling = DETAILED_CULLING_VALUE.get();
            conTexMode = CON_TEX_MODE_VALUE.get();
            camoMessageVerbosity = CAMO_MESSAGE_VERBOSITY_VALUE.get();
            forceAoOnGlowingBlocks = FORCE_AO_ON_GLOWING_BLOCKS_VALUE.get();
            renderItemModelsWithCamo = RENDER_ITEM_MODELS_WITH_CAMO_VALUE.get();
            showAllRecipePermutationsInEmi = SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE.get();
            solidFrameMode = SOLID_FRAME_MODE_VALUE.get();
            showButtonPlateOverlay = SHOW_BUTTON_PLATE_OVERLAY_VALUE.get();
            showSpecialCubeOverlay = SHOW_SPECIAL_CUBE_OVERLAY_VALUE.get();
            renderCamoInJade = RENDER_CAMO_IN_JADE_VALUE.get();
            showCamoCraftingInJei = SHOW_CAMO_CRAFTING_IN_JEI_VALUE.get();

            maxOverlayMode = MAX_OVERLAY_MODE_VALUE.get();
            stateLockMode = STATE_LOCK_MODE_VALUE.get();
            toggleWaterlogMode = TOGGLE_WATERLOG_MODE_VALUE.get();
            toggleAltSlopeMode = TOGGLE_ALT_SLOPE_MODE_VALUE.get();
            reinforcementMode = REINFORCEMENT_MODE_VALUE.get();
            prismOffsetMode = PRISM_OFFSET_MODE_VALUE.get();
            splitLineMode = SPLIT_LINE_MODE_VALUE.get();
            oneWayWindowMode = ONE_WAY_WINDOW_MODE_VALUE.get();
            frameBackgroundMode = FRAME_BACKGROUND_MODE_VALUE.get();
            camoRotationMode = CAMO_ROTATION_MODE_VALUE.get();
            trapdoorTextureRotationMode = TRAPDOOR_TEXTURE_ROTATION_MODE_VALUE.get();
            copycatStyleMode = COPYCAT_STYLE_MODE_VALUE.get();
        }
    }

    private ClientConfig() { }

    public static final class ViewImpl implements ExtConfigView.Client {
        @Override
        public boolean showGhostBlocks() {
            return showGhostBlocks;
        }

        @Override
        public boolean useAltGhostRenderer() {
            return altGhostRenderer;
        }

        @Override
        public int getGhostRenderOpacity() {
            return ghostRenderOpacity;
        }

        @Override
        public boolean useFancySelectionBoxes() {
            return fancyHitboxes;
        }

        @Override
        public boolean detailedCullingEnabled() {
            return detailedCulling;
        }

        @Override
        public ConTexMode getConTexMode() {
            return conTexMode;
        }

        @Override
        public CamoMessageVerbosity getCamoMessageVerbosity() {
            return camoMessageVerbosity;
        }

        @Override
        public boolean shouldForceAmbientOcclusionOnGlowingBlocks() {
            return forceAoOnGlowingBlocks;
        }

        @Override
        public boolean shouldRenderItemModelsWithCamo() {
            return renderItemModelsWithCamo;
        }

        @Override
        public boolean showAllRecipePermutationsInEmi() {
            return showAllRecipePermutationsInEmi;
        }

        @Override
        public SolidFrameMode getSolidFrameMode() {
            return solidFrameMode;
        }

        @Override
        public boolean showButtonPlateOverlay() {
            return showButtonPlateOverlay;
        }

        @Override
        public boolean showSpecialCubeOverlay() {
            return showSpecialCubeOverlay;
        }

        @Override
        public boolean shouldRenderCamoInJade() {
            return renderCamoInJade;
        }

        @Override
        public boolean showCamoCraftingInJei() {
            return showCamoCraftingInJei;
        }

        @Override
        public OverlayDisplayMode getMaxOverlayMode() {
            return maxOverlayMode;
        }

        @Override
        public OverlayDisplayMode getStateLockMode() {
            return stateLockMode;
        }

        @Override
        public OverlayDisplayMode getToggleWaterlogMode() {
            return toggleWaterlogMode;
        }

        @Override
        public OverlayDisplayMode getToggleAltSlopeMode() {
            return toggleAltSlopeMode;
        }

        @Override
        public OverlayDisplayMode getReinforcementMode() {
            return reinforcementMode;
        }

        @Override
        public OverlayDisplayMode getPrismOffsetMode() {
            return prismOffsetMode;
        }

        @Override
        public OverlayDisplayMode getSplitLineMode() {
            return splitLineMode;
        }

        @Override
        public OverlayDisplayMode getOneWayWindowMode() {
            return oneWayWindowMode;
        }

        @Override
        public OverlayDisplayMode getFrameBackgroundMode() {
            return frameBackgroundMode;
        }

        @Override
        public OverlayDisplayMode getCamoRotationMode() {
            return camoRotationMode;
        }

        @Override
        public OverlayDisplayMode getTrapdoorTextureRotationMode() {
            return trapdoorTextureRotationMode;
        }

        @Override
        public OverlayDisplayMode getCopycatStyleMode() {
            return copycatStyleMode;
        }
    }
}
